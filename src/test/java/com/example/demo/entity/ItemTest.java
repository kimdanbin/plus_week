package com.example.demo.entity;

import com.example.demo.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class ItemTest {

    @Autowired
    private ItemRepository itemRepository;

    // 8. status가 null일때 예외가 발생하지 않는지 테스트
    @Test
    @Transactional
    public void test() {
        // given
        Item item = new Item("asdf", "asdf", new User(), null);

        // when, then
        assertDoesNotThrow(() -> itemRepository.save(item));

    }

}