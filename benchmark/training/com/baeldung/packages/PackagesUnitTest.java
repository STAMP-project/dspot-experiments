package com.baeldung.packages;


import com.baeldung.packages.domain.TodoItem;
import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;


public class PackagesUnitTest {
    @Test
    public void whenTodoItemAdded_ThenSizeIncreases() {
        TodoItem todoItem = new TodoItem();
        todoItem.setId(1L);
        todoItem.setDescription("Test the Todo List");
        todoItem.setDueDate(LocalDate.now());
        TodoList todoList = new TodoList();
        todoList.addTodoItem(todoItem);
        Assert.assertEquals(1, todoList.getTodoItems().size());
    }
}

