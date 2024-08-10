package com.kert.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class History {
    //제 생각은 이게 아니었는데 identifier가 없으면 안된대서 넣었어요
    //이게 리스트형태로 나중에 나가야하는데 pubilc class List<History>로는 안되길래
    //oneTomany manyToone으로 구성했는데 이러니까 id가 2개 들어가네요 어째야할까요
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoryList> history;
}

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
class HistoryList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int year;
    private int month;
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private History history;
}
