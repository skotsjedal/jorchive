package no.skotsj.jorchive.web.vaadin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.*;
import no.skotsj.jorchive.common.domain.EntryType;
import no.skotsj.jorchive.common.domain.FolderType;
import no.skotsj.jorchive.service.ArchiveService;
import no.skotsj.jorchive.web.model.FileInfo;
import no.skotsj.jorchive.web.model.FileList;
import no.skotsj.jorchive.web.vaadin.filter.EntryTypeFilter;
import no.skotsj.jorchive.web.vaadin.filter.NameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.navigator.annotation.VaadinView;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Main view containt the application
 */
@VaadinView(name = MainUi.MAINVIEW)
public class MainView extends VerticalLayout implements View, InitializingBean
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
    }

    private void init()
    {
        setMargin(true);

        initTop();

        addComponent(files);

        initFileList();
        initFiles(archiveService.listDownloaded());

        setExpandRatio(files, 1);
        files.setSizeFull();

        initBottom();

        initSearch();
    }

    private void initTop()
    {
        HorizontalLayout topLayout = new HorizontalLayout();
        addComponent(topLayout);

        Label title = new Label("Jorchive");
        title.setStyleName("mainTitle");
        title.setWidth("70%");
        topLayout.addComponent(title);

        List<FolderType> folders = Lists.newArrayList(FolderType.DOWN, FolderType.MOVIE, FolderType.TV, FolderType.TEMP);
        OptionGroup optionGroup = new OptionGroup("Folders", folders);
        optionGroup.select(FolderType.DOWN);
        optionGroup.setStyleName("horizontal");
        optionGroup.addStyleName("right");
        optionGroup.addValueChangeListener(event -> changeList((FolderType) event.getProperty().getValue()));
        topLayout.addComponent(optionGroup);
    }

    private void changeList(FolderType value)
    {
        switch (value)
        {
            case DOWN:
                initFiles(archiveService.listDownloaded());
                break;
            case TEMP:
                initFiles(archiveService.listTemp());
                break;
            case TV:
                initFiles(archiveService.listTv());
                break;
            case MOVIE:
                initFiles(archiveService.listMovie());
                break;
            case MOVIE_ARCHIVE:
                initFiles(archiveService.listMovieArchive());
                break;
            case ANIME:
                initFiles(archiveService.listAnime());
                break;
            default:
                log.error("NYI");
        }
    }

    private void initBottom()
    {
        HorizontalLayout bottomLayout = new HorizontalLayout();
        addComponent(bottomLayout);

        bottomLayout.setMargin(true);
        bottomLayout.addComponent(searchField);

        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setSpacing(true);
        bottomLayout.addComponent(filterLayout);
        filterLayout.addComponent(new Label("Filter type:"));
        for (EntryType entryType : EntryType.values())
        {
            filterLayout.addComponent(new Button(entryType.getValue(),
                    event -> filter(new EntryTypeFilter(entryType))));
        }
        filterLayout.addComponent(new Button("None", clearEntryTypeFilter()));

        setSizeFull();


        bottomLayout.setWidth("100%");
        searchField.setWidth("95%");
        bottomLayout.setExpandRatio(searchField, 1);
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
        fileContainer = new IndexedContainer();
        fileContainer.addContainerProperty(NAME, Label.class, null);
        fileContainer.addContainerProperty(SIZE, Label.class, null);
        fileContainer.addContainerProperty(TYPE, String.class, "");
        fileContainer.addContainerProperty(ACTION, Button.class, null);

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
    private void initFiles(List<Path> list)
    {
        fileContainer.removeAllItems();

        for (FileInfo f : new FileList(list).getFiles())
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
            if (f.getEntryType() == EntryType.ARCHIVE_ENTRY)
            {
                fileContainer.getContainerProperty(id, ACTION).setValue(
                        new Button("Extract", event -> archiveService.extract(id)));

            } else
            {
                fileContainer.getContainerProperty(id, ACTION).setValue(
                        new Button("Copy", event -> archiveService.copyFromInput(id)));

            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        init();
    }
}
