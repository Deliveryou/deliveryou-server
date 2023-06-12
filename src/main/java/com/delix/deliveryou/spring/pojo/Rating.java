package com.delix.deliveryou.spring.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "rating")
public class Rating {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "package_id")
    private DeliveryPackage deliveryPackage;

    @Column(name = "content")
    private String content;

    @Column(name = "rate")
    private float rate;

    @Column(name = "marked")
    private boolean marked;

    @Column(name = "date")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime date;

    public Rating(DeliveryPackage deliveryPackage, String content, float rate, boolean marked, LocalDateTime date) {
        this.deliveryPackage = deliveryPackage;
        this.content = content;
        this.rate = rate;
        this.marked = marked;
        this.date = date;
    }
}
