package com.craftsmanship.tfm.persistence;

import java.util.List;

import com.craftsmanship.tfm.exceptions.ItemAlreadyExists;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.models.DomainItem;

public interface ItemPersistence {
    public DomainItem create(DomainItem item) throws ItemAlreadyExists;
    public List<DomainItem> list();
    public DomainItem get(Long id) throws ItemDoesNotExist;
    public DomainItem update(Long id, DomainItem item) throws ItemDoesNotExist;
    public DomainItem delete(Long id) throws ItemDoesNotExist;
}