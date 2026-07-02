package com.iot.platform.service.impl;

import com.iot.platform.entity.Product;
import com.iot.platform.entity.ThingProperty;
import com.iot.platform.entity.ThingService;
import com.iot.platform.entity.ThingEvent;
import com.iot.platform.repository.*;
import com.iot.platform.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ThingPropertyRepository propertyRepository;
    private final ThingServiceRepository serviceRepository;
    private final ThingEventRepository eventRepository;
    private final DeviceRepository deviceRepository;

    @Override
    @Transactional
    public Product createProduct(Product product) {
        product.setId(generateId("P"));
        product.setCreatedTime(LocalDateTime.now());
        product.setUpdatedTime(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(String id, Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("产品不存在: " + id));
        existing.setName(product.getName());
        existing.setType(product.getType());
        existing.setDescription(product.getDescription());
        existing.setUpdatedTime(LocalDateTime.now());
        return productRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("产品不存在: " + id);
        }
        // 删除保护：有设备时禁止删除
        long deviceCount = deviceRepository.countByProductId(id);
        if (deviceCount > 0) {
            throw new RuntimeException("该产品下还有 " + deviceCount + " 个设备，请先删除设备");
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
        property.setCreatedTime(LocalDateTime.now());
        property.setUpdatedTime(LocalDateTime.now());
        return propertyRepository.save(property);
    }

    @Override
    @Transactional
    public ThingProperty updateProperty(String propertyId, ThingProperty property) {
        ThingProperty existing = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("属性不存在: " + propertyId));
        existing.setName(property.getName());
        existing.setIdentifier(property.getIdentifier());
        existing.setDataType(property.getDataType());
        existing.setMinValue(property.getMinValue());
        existing.setMaxValue(property.getMaxValue());
        existing.setUnit(property.getUnit());
        existing.setAccessMode(property.getAccessMode());
        existing.setUpdatedTime(LocalDateTime.now());
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
        service.setId(generateId("SVC"));
        service.setProduct(product);
        service.setCreatedTime(LocalDateTime.now());
        service.setUpdatedTime(LocalDateTime.now());
        return serviceRepository.save(service);
    }

    @Override
    @Transactional
    public ThingService updateService(String serviceId, ThingService service) {
        ThingService existing = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("服务不存在: " + serviceId));
        existing.setName(service.getName());
        existing.setIdentifier(service.getIdentifier());
        existing.setInputParams(service.getInputParams());
        existing.setOutputParams(service.getOutputParams());
        existing.setDescription(service.getDescription());
        existing.setUpdatedTime(LocalDateTime.now());
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
        event.setCreatedTime(LocalDateTime.now());
        event.setUpdatedTime(LocalDateTime.now());
        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public ThingEvent updateEvent(String eventId, ThingEvent event) {
        ThingEvent existing = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("事件不存在: " + eventId));
        existing.setName(event.getName());
        existing.setIdentifier(event.getIdentifier());
        existing.setEventType(event.getEventType());
        existing.setDescription(event.getDescription());
        existing.setUpdatedTime(LocalDateTime.now());
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

    private String generateId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
