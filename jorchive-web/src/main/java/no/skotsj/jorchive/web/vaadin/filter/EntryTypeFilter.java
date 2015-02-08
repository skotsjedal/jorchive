package no.skotsj.jorchive.web.vaadin.filter;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import no.skotsj.jorchive.common.domain.EntryType;

import static no.skotsj.jorchive.web.vaadin.MainView.TYPE;

/**
 * Filter on type
 */
public class EntryTypeFilter implements Container.Filter
{
    private final EntryType entryType;

    public EntryTypeFilter(EntryType entryType)
    {
        this.entryType = entryType;
    }

    @Override
    public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException
    {
        return item.getItemProperty(TYPE).getValue().equals(entryType.getValue());
    }

    @Override
    public boolean appliesToProperty(Object propertyId)
    {
        return TYPE.equals(propertyId);
    }
}
