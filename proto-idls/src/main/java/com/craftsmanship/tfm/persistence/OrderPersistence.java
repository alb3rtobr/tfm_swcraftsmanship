package com.craftsmanship.tfm.persistence;

import java.util.List;

import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;
import com.craftsmanship.tfm.models.DomainOrder;

public interface OrderPersistence {
    public DomainOrder create(DomainOrder order) throws ItemDoesNotExist;
    public List<DomainOrder> list();
    public DomainOrder get(Long id) throws OrderDoesNotExist;
    public DomainOrder update(Long id, DomainOrder order) throws OrderDoesNotExist, ItemDoesNotExist;
    public DomainOrder delete(Long id) throws OrderDoesNotExist;
}