package ecommerce.inventory_service;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
public class Inventory {
    @Id
    private String id;
    private String productId;
    private Integer quantity;
    private Integer reserved;
    private LocalDateTime updateTime;
}
