package no.skotsj.jorchive.web.vaadin.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.ui.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.skotsj.jorchive.web.vaadin.MainView.NAME;

/**
 * Filter for name
 */
public class NameFilter implements Filter
{

    private static Logger log = LoggerFactory.getLogger(NameFilter.class);

    private String needle;

    public NameFilter(String needle)
    {
        this.needle = needle;
        log.debug("Filtering {}", needle);
    }

    @Override
    public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException
    {
        Label fileNameLabel = (Label) item.getItemProperty(NAME).getValue();
        return fileNameLabel != null && fileNameLabel.getValue().contains(needle);
    }

    @Override
    public boolean appliesToProperty(Object propertyId)
    {
        return NAME.equals(propertyId);
    }
}
