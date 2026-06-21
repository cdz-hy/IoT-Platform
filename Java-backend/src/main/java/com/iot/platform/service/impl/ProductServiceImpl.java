package com.iot.platform.service.impl;

import com.iot.platform.entity.Product;
import com.iot.platform.entity.ThingProperty;
import com.iot.platform.entity.ThingService;
import com.iot.platform.entity.ThingEvent;
import com.iot.platform.repository.ProductRepository;
import com.iot.platform.repository.ThingPropertyRepository;
import com.iot.platform.repository.ThingServiceRepository;
import com.iot.platform.repository.ThingEventRepository;
import com.iot.platform.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 产品服务实现类
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ThingPropertyRepository propertyRepository;
    private final ThingServiceRepository serviceRepository;
    private final ThingEventRepository eventRepository;

    @Override
    @Transactional
    public Product createProduct(Product product) {
        // 生成产品ID
        product.setId(generateId("PROD"));
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(String id, Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("产品不存在: " + id));
        existing.setName(product.getName());
        existing.setType(product.getType());
        return productRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("产品不存在: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("产品不存在: " + id));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public ThingProperty addProperty(String productId, ThingProperty property) {
        Product product = getProductById(productId);
        property.setId(generateId("PROP"));
        property.setProduct(product);
        return propertyRepository.save(property);
    }

    @Override
    @Transactional
    public ThingProperty updateProperty(String propertyId, ThingProperty property) {
        ThingProperty existing = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("属性不存在: " + propertyId));
        existing.setPropertyName(property.getPropertyName());
        existing.setDataType(property.getDataType());
        existing.setMinValue(property.getMinValue());
        existing.setMaxValue(property.getMaxValue());
        existing.setUnit(property.getUnit());
        existing.setAccessMode(property.getAccessMode());
        return propertyRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteProperty(String propertyId) {
        propertyRepository.deleteById(propertyId);
    }

    @Override
    public List<ThingProperty> getPropertiesByProductId(String productId) {
        return propertyRepository.findByProductId(productId);
    }

    @Override
    @Transactional
    public ThingService addService(String productId, ThingService service) {
        Product product = getProductById(productId);
        service.setId(generateId("SERV"));
        service.setProduct(product);
        return serviceRepository.save(service);
    }

    @Override
    @Transactional
    public ThingService updateService(String serviceId, ThingService service) {
        ThingService existing = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("服务不存在: " + serviceId));
        existing.setServiceName(service.getServiceName());
        existing.setInputParams(service.getInputParams());
        existing.setOutputParams(service.getOutputParams());
        return serviceRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteService(String serviceId) {
        serviceRepository.deleteById(serviceId);
    }

    @Override
    public List<ThingService> getServicesByProductId(String productId) {
        return serviceRepository.findByProductId(productId);
    }

    @Override
    @Transactional
    public ThingEvent addEvent(String productId, ThingEvent event) {
        Product product = getProductById(productId);
        event.setId(generateId("EVT"));
        event.setProduct(product);
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public ThingEvent updateEvent(String eventId, ThingEvent event) {
        ThingEvent existing = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("事件不存在: " + eventId));
        existing.setEventName(event.getEventName());
        existing.setEventType(event.getEventType());
        existing.setDescription(event.getDescription());
        return eventRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteEvent(String eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public List<ThingEvent> getEventsByProductId(String productId) {
        return eventRepository.findByProductId(productId);
    }

    @Override
    public Map<String, Long> getProductCountByType() {
        List<Object[]> results = productRepository.countByType();
        return results.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long) r[1]
                ));
    }

    /**
     * 生成唯一ID
     */
    private String generateId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
