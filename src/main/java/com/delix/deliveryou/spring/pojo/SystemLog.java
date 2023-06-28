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
@Table(name = "server_log")
public class SystemLog {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "event")
    private String event;

    @Column(name = "time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a")
    private LocalDateTime date;

    @Column(name = "previous_data")
    private String previousData;

    @Column(name = "new_data")
    private String newData;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

    public SystemLog(String event, LocalDateTime date, String previousData, String newData, User user) {
        this.event = event;
        this.date = date;
        this.previousData = previousData;
        this.newData = newData;
        this.user = user;
    }
}
