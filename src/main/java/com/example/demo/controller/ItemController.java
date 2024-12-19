package com.example.demo.controller;

import com.example.demo.dto.ItemRequestDto;
import com.example.demo.entity.Item;
import com.example.demo.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody ItemRequestDto itemRequestDto) {
        Item item = itemService.createItem(itemRequestDto.getName(),
                itemRequestDto.getDescription(),
                itemRequestDto.getOwnerId(),
                itemRequestDto.getManagerId());

        return ResponseEntity.ok().body(item); // 7-2 응답 데이터 타입 변경
    }
}
