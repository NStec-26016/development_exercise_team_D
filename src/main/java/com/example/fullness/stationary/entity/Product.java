package com.example.fullness.stationary.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class Product implements Serializable {
    private Integer id;
<<<<<<< HEAD
=======
    private Integer productCategoryId;

>>>>>>> origin/development2
    private String name;
    private Integer price;
    private String imageUrl;
    private Integer productCategoryId;
    private Integer deleteFlag;
<<<<<<< HEAD
    
}
=======

    private Integer stock; // ★ 追加

}
>>>>>>> origin/development2
