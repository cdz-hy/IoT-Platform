package com.iot.platform.service;

import com.iot.platform.entity.Product;
import com.iot.platform.entity.ThingProperty;
import com.iot.platform.entity.ThingService;
import com.iot.platform.entity.ThingEvent;

import java.util.List;
import java.util.Map;

/**
 * 产品服务接口
 */
public interface ProductService {

    /** 创建产品 */
    Product createProduct(Product product);

    /** 更新产品 */
    Product updateProduct(String id, Product product);

    /** 删除产品（级联删除关联的设备和物模型） */
    void deleteProduct(String id);

    /** 根据ID查询产品 */
    Product getProductById(String id);

    /** 查询所有产品 */
    List<Product> getAllProducts();

    /** 添加物模型属性 */
    ThingProperty addProperty(String productId, ThingProperty property);

    /** 更新物模型属性 */
    ThingProperty updateProperty(String propertyId, ThingProperty property);

    /** 删除物模型属性 */
    void deleteProperty(String propertyId);

    /** 查询产品的所有属性 */
    List<ThingProperty> getPropertiesByProductId(String productId);

    /** 添加物模型服务 */
    ThingService addService(String productId, ThingService service);

    /** 更新物模型服务 */
    ThingService updateService(String serviceId, ThingService service);

    /** 删除物模型服务 */
    void deleteService(String serviceId);

    /** 查询产品的所有服务 */
    List<ThingService> getServicesByProductId(String productId);

    /** 添加物模型事件 */
    ThingEvent addEvent(String productId, ThingEvent event);

    /** 更新物模型事件 */
    ThingEvent updateEvent(String eventId, ThingEvent event);

    /** 删除物模型事件 */
    void deleteEvent(String eventId);

    /** 查询产品的所有事件 */
    List<ThingEvent> getEventsByProductId(String productId);

    /** 统计各类型产品数量 */
    Map<String, Long> getProductCountByType();
}
