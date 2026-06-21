package com.iot.platform.controller;

import com.iot.platform.annotation.OperationLog;
import com.iot.platform.dto.ApiResponse;
import com.iot.platform.entity.Product;
import com.iot.platform.entity.ThingProperty;
import com.iot.platform.entity.ThingService;
import com.iot.platform.entity.ThingEvent;
import com.iot.platform.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 产品管理控制器
 */
@Tag(name = "产品管理", description = "产品CRUD和物模型管理")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 创建产品
     */
    @Operation(summary = "创建产品")
    @OperationLog(module = "产品管理", type = "CREATE", description = "创建产品")
    @PostMapping
    public ApiResponse<Product> createProduct(@RequestBody Product product) {
        return ApiResponse.success(productService.createProduct(product));
    }

    /**
     * 更新产品
     */
    @Operation(summary = "更新产品")
    @PutMapping("/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        return ApiResponse.success(productService.updateProduct(id, product));
    }

    /**
     * 删除产品
     */
    @Operation(summary = "删除产品")
    @OperationLog(module = "产品管理", type = "DELETE", description = "删除产品")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ApiResponse.success(null);
    }

    /**
     * 查询产品详情
     */
    @Operation(summary = "查询产品详情")
    @GetMapping("/{id}")
    public ApiResponse<Product> getProduct(@PathVariable String id) {
        return ApiResponse.success(productService.getProductById(id));
    }

    /**
     * 查询所有产品
     */
    @Operation(summary = "查询所有产品")
    @GetMapping
    public ApiResponse<List<Product>> getAllProducts() {
        return ApiResponse.success(productService.getAllProducts());
    }

    /**
     * 统计各类型产品数量
     */
    @Operation(summary = "统计各类型产品数量")
    @GetMapping("/statistics/type")
    public ApiResponse<Map<String, Long>> getProductCountByType() {
        return ApiResponse.success(productService.getProductCountByType());
    }

    // ==================== 物模型属性管理 ====================

    /**
     * 添加产品属性
     */
    @Operation(summary = "添加产品属性")
    @PostMapping("/{productId}/properties")
    public ApiResponse<ThingProperty> addProperty(@PathVariable String productId,
                                                   @RequestBody ThingProperty property) {
        return ApiResponse.success(productService.addProperty(productId, property));
    }

    /**
     * 更新产品属性
     */
    @Operation(summary = "更新产品属性")
    @PutMapping("/properties/{propertyId}")
    public ApiResponse<ThingProperty> updateProperty(@PathVariable String propertyId,
                                                      @RequestBody ThingProperty property) {
        return ApiResponse.success(productService.updateProperty(propertyId, property));
    }

    /**
     * 删除产品属性
     */
    @Operation(summary = "删除产品属性")
    @DeleteMapping("/properties/{propertyId}")
    public ApiResponse<Void> deleteProperty(@PathVariable String propertyId) {
        productService.deleteProperty(propertyId);
        return ApiResponse.success(null);
    }

    /**
     * 查询产品所有属性
     */
    @Operation(summary = "查询产品所有属性")
    @GetMapping("/{productId}/properties")
    public ApiResponse<List<ThingProperty>> getProperties(@PathVariable String productId) {
        return ApiResponse.success(productService.getPropertiesByProductId(productId));
    }

    // ==================== 物模型服务管理 ====================

    /**
     * 添加产品服务
     */
    @Operation(summary = "添加产品服务")
    @PostMapping("/{productId}/services")
    public ApiResponse<ThingService> addService(@PathVariable String productId,
                                                 @RequestBody ThingService service) {
        return ApiResponse.success(productService.addService(productId, service));
    }

    /**
     * 更新产品服务
     */
    @Operation(summary = "更新产品服务")
    @PutMapping("/services/{serviceId}")
    public ApiResponse<ThingService> updateService(@PathVariable String serviceId,
                                                    @RequestBody ThingService service) {
        return ApiResponse.success(productService.updateService(serviceId, service));
    }

    /**
     * 删除产品服务
     */
    @Operation(summary = "删除产品服务")
    @DeleteMapping("/services/{serviceId}")
    public ApiResponse<Void> deleteService(@PathVariable String serviceId) {
        productService.deleteService(serviceId);
        return ApiResponse.success(null);
    }

    /**
     * 查询产品所有服务
     */
    @Operation(summary = "查询产品所有服务")
    @GetMapping("/{productId}/services")
    public ApiResponse<List<ThingService>> getServices(@PathVariable String productId) {
        return ApiResponse.success(productService.getServicesByProductId(productId));
    }

    // ==================== 物模型事件管理 ====================

    /**
     * 添加产品事件
     */
    @Operation(summary = "添加产品事件")
    @PostMapping("/{productId}/events")
    public ApiResponse<ThingEvent> addEvent(@PathVariable String productId,
                                             @RequestBody ThingEvent event) {
        return ApiResponse.success(productService.addEvent(productId, event));
    }

    /**
     * 更新产品事件
     */
    @Operation(summary = "更新产品事件")
    @PutMapping("/events/{eventId}")
    public ApiResponse<ThingEvent> updateEvent(@PathVariable String eventId,
                                                @RequestBody ThingEvent event) {
        return ApiResponse.success(productService.updateEvent(eventId, event));
    }

    /**
     * 删除产品事件
     */
    @Operation(summary = "删除产品事件")
    @DeleteMapping("/events/{eventId}")
    public ApiResponse<Void> deleteEvent(@PathVariable String eventId) {
        productService.deleteEvent(eventId);
        return ApiResponse.success(null);
    }

    /**
     * 查询产品所有事件
     */
    @Operation(summary = "查询产品所有事件")
    @GetMapping("/{productId}/events")
    public ApiResponse<List<ThingEvent>> getEvents(@PathVariable String productId) {
        return ApiResponse.success(productService.getEventsByProductId(productId));
    }
}
