package com.example.demo.dto.post;


import com.example.demo.entity.Post.Hot;
import com.example.demo.entity.Post.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotDto {
    private Post post;
    private int weight;

    public HotDto(Hot hot) {
        this.post = hot.getPost();
        this.weight = hot.getWeight();
    }
}
