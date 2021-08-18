package com.charles.heap;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author chales.tang
 * @title HeapLeakTest
 * @discription 堆内存泄漏测试
 */
public class HeapLeakTest {

    public static void main(String[] args) {

        HashMap<Car, Integer> hashMap = new HashMap<>(1000);
        int count = 0;
        while (true) {
            Car car = new Car("林肯", "007");
            hashMap.put(car, count);
            count++;
            if (count % 1000 == 0) {
                System.out.println("map size: " + hashMap.size());
                System.out.println("运行" + count + "次后，可用内存剩余" +
                        Runtime.getRuntime().freeMemory() / (1024 * 1024) + "MB");
            }
        }
    }

    static class Car {

        private String name;
        private String id;

        public Car(String name, String id) {
            this.name = name;
            this.id = id;
        }

//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            Car car = (Car) o;
//            return Objects.equals(name, car.name) &&
//                    Objects.equals(id, car.id);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(name, id);
//        }
    }
}
