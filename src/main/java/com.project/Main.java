package com.project;

import custom.CustomHashMap;

import java.util.HashMap;

public class Main {
    static void main(String[] args) {
        CustomHashMap<Integer, String> map = new CustomHashMap<Integer, String>();
        System.out.println("Добавление записей в карту");
        map.put(null, "Ничего");
        map.put(1, "ETC");
        map.put(2, "John");
        System.out.println("Отображение всех записей в карте");
        map.display();
        System.out.println("Удаление записи с ключом 2");
        map.remove(2);
        map.display();
        System.out.println("Добавление дубликата ключа 1 в карту");
        map.put(1, "CSE");
        map.put(2, "John снова");
        System.out.println("Отображение всех записей в карте снова");
        map.display();
        System.out.println("Добавление записи с ключом 17 в карту");
        map.put(17, "CS");
        map.display();
    }
}
