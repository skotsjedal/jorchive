package no.skotsj.jorchive.web.vaadin;

import com.google.common.collect.Maps;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.*;
import no.skotsj.jorchive.common.domain.EntryType;
import no.skotsj.jorchive.service.ArchiveService;
import no.skotsj.jorchive.web.model.FileInfo;
import no.skotsj.jorchive.web.model.FileList;
import no.skotsj.jorchive.web.vaadin.filter.EntryTypeFilter;
import no.skotsj.jorchive.web.vaadin.filter.NameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.navigator.annotation.VaadinView;

import java.util.Map;

/**
 * Main view containt the application
 */
@VaadinView(name = MainUi.MAINVIEW)
public class MainView extends VerticalLayout implements View
{

    private static Logger log = LoggerFactory.getLogger(MainView.class);

    public static final String NAME = "Name";
    public static final String SIZE = "Size";
    public static final String TYPE = "Type";
    public static final String ACTION = "Action";

    private Table files = new Table();
    private TextField searchField = new TextField();

    @Autowired
    private ArchiveService archiveService;
    @Autowired
    private LoginView loginView;

    private IndexedContainer fileContainer;

    private Map<Class, Filter> filters = Maps.newHashMap();

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewEvent)
    {
        if (!loginView.isAuthed())
        {
            // improve this
            getUI().getNavigator().navigateTo("");
        }

        setMargin(true);
        addComponent(files);

        FileList fileList = new FileList(archiveService.listCompleted());
        initFiles(fileList);

        HorizontalLayout bottomLeftLayout = new HorizontalLayout();
        addComponent(bottomLeftLayout);
        bottomLeftLayout.addComponent(searchField);

        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setSpacing(true);
        bottomLeftLayout.addComponent(filterLayout);
        filterLayout.addComponent(new Label("Filter type:"));
        for (EntryType entryType : EntryType.values())
        {
            filterLayout.addComponent(new Button(entryType.getValue(),
                    event -> filter(new EntryTypeFilter(entryType))));
        }
        filterLayout.addComponent(new Button("None", clearEntryTypeFilter()));

        setSizeFull();

        setExpandRatio(files, 1);
        files.setSizeFull();

        bottomLeftLayout.setWidth("90%");
        searchField.setWidth("90%");
        bottomLeftLayout.setExpandRatio(searchField, 1);

        //infoLayout.setMargin(true);
        //infoLayout.setVisible(false);

        initFileList();
        initSearch();

    }

    private Button.ClickListener clearEntryTypeFilter()
    {
        return event -> {
            if (filters.containsKey(EntryTypeFilter.class))
            {
                fileContainer.removeContainerFilter(filters.get(EntryTypeFilter.class));
                filters.remove(EntryTypeFilter.class);
            }
        };
    }

    private void filter(Filter filter)
    {
        Class<? extends Filter> filterClass = filter.getClass();
        if (filters.containsKey(filterClass))
        {
            fileContainer.removeContainerFilter(filters.get(filterClass));
        }
        filters.put(filterClass, filter);
        fileContainer.addContainerFilter(filter);
    }

    private void initSearch()
    {
        searchField.setInputPrompt("Search");
        searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);
        searchField.addTextChangeListener(event -> filter(new NameFilter(event.getText())));
    }

    private void initFileList()
    {
        files.setContainerDataSource(fileContainer);
        files.setSelectable(true);
        files.setImmediate(true);

        files.addItemClickListener(event -> {
            log.info("clicked {}", event.getItemId());

            //if (fileId != null)
            //editorFields.setItemDataSource(files.getItem(fileId));

            //infoLayout.setVisible(fileId != null);
        });
    }

    @SuppressWarnings("unchecked")
    private void initFiles(FileList fileList)
    {
        fileContainer = new IndexedContainer();

        fileContainer.addContainerProperty(NAME, Label.class, "");
        fileContainer.addContainerProperty(SIZE, Label.class, "");
        fileContainer.addContainerProperty(TYPE, String.class, "");
        fileContainer.addContainerProperty(ACTION, Button.class, null);

        for (FileInfo f : fileList.getFiles())
        {
            if (f.isIgnored())
            {
                continue;
            }

            String id = f.getId();
            fileContainer.addItem(id);

            Label name = new Label(f.getName(), ContentMode.HTML);
            fileContainer.getContainerProperty(id, NAME).setValue(name);

            Label size = new Label(f.getHtmlSize(), ContentMode.HTML);
            fileContainer.getContainerProperty(id, SIZE).setValue(size);

            fileContainer.getContainerProperty(id, TYPE).setValue(f.getEntryType().getValue());
            switch (f.getEntryType())
            {
                case ARCHIVE_ENTRY:
                    fileContainer.getContainerProperty(id, ACTION).setValue(
                            new Button("Extract", event -> archiveService.extract(id)));
                    break;
                default:
                    fileContainer.getContainerProperty(id, ACTION).setValue(
                            new Button("Copy", event -> archiveService.copyFromInput(id)));
                    break;
            }
        }

    }
}
