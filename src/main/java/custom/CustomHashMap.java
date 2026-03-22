package custom;

import java.util.Objects;

public class CustomHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Node<K, V>[] buckets;
    private int size;
    private int threshold;

    public CustomHashMap() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public CustomHashMap(int initialCapacity) {
        buckets = (Node<K, V>[]) new Node[initialCapacity];
        threshold = (int) (initialCapacity * DEFAULT_LOAD_FACTOR);
    }

    private static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    public void put(K key, V value) {
        int hash = hash(key);
        int index = indexFor(hash, buckets.length);

        Node<K, V> node = buckets[index];

        while (node != null) {
            if (node.hash == hash && equalsKey(node.key, key)) {
                node.value = value;
                return;
            }
            node = node.next;
        }

        Node<K, V> newNode = new Node<>(hash, key, value, buckets[index]);
        buckets[index] = newNode;
        size++;

        if (size >= threshold) {
            resize();
        }
    }

    public V get(K key) {
        int hash = hash(key);
        int index = indexFor(hash, buckets.length);

        Node<K, V> node = buckets[index];
        while (node != null) {
            if (node.hash == hash && equalsKey(node.key, key)) {
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    public V remove(K key) {
        int hash = hash(key);
        int index = indexFor(hash, buckets.length);

        Node<K, V> node = buckets[index];
        Node<K, V> prev = null;

        while (node != null) {
            if (node.hash == hash && equalsKey(node.key, key)) {
                V oldValue = node.value;
                if (prev == null) {
                    buckets[index] = node.next; // удаляем голову
                } else {
                    prev.next = node.next; // удаляем из середины/конца
                }
                size--;
                return oldValue;
            }
            prev = node;
            node = node.next;
        }
        return null;
    }

    private boolean equalsKey(K key1, K key2) {
        return Objects.equals(key1, key2);
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldBuckets = buckets;
        int newCapacity = oldBuckets.length * 2;
        Node<K, V>[] newBuckets = (Node<K, V>[]) new Node[newCapacity];

        for (Node<K, V> node : oldBuckets) {
            while (node != null) {
                Node<K, V> next = node.next;
                int newIndex = (node.hash & (newCapacity - 1));
                node.next = newBuckets[newIndex];
                newBuckets[newIndex] = node;
                node = next;
            }
        }

        buckets = newBuckets;
        threshold = (int) (newCapacity * DEFAULT_LOAD_FACTOR);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void display() {
        System.out.println("=== Содержимое MyHashMap ===");
        System.out.println("Размер: " + size);
        System.out.println("Ёмкость: " + buckets.length);
        System.out.println("Порог расширения: " + threshold);
        System.out.println("Корзины:");

        for (int i = 0; i < buckets.length; i++) {
            Node<K, V> node = buckets[i];

            if (node != null) {
                System.out.print("  Корзина " + i + ": ");

                while (node != null) {
                    System.out.print(node.key + "=" + node.value);
                    if (node.next != null) {
                        System.out.print(" → ");
                    }
                    node = node.next;
                }
                System.out.println();
            }
        }
        System.out.println("=============================");
    }
}