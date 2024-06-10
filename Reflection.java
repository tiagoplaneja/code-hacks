import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class TransformToString {

    public static void transformAttributesToString(Object obj) {
        if (obj == null) {
            return;
        }

        Class<?> objClass = obj.getClass();

        // Iterate over all fields of the class
        for (Field field : objClass.getDeclaredFields()) {
            field.setAccessible(true);  // Ensure the field is accessible
            try {
                Object value = field.get(obj);

                if (value == null) {
                    continue;
                }

                // If the field is a primitive type or String, transform it to String
                if (value instanceof String || isPrimitiveWrapper(value.getClass())) {
                    field.set(obj, value.toString());
                } 
                // If the field is a Collection, iterate over its elements and transform them
                else if (value instanceof Collection) {
                    for (Object item : (Collection<?>) value) {
                        transformAttributesToString(item);
                    }
                }
                // If the field is a Map, iterate over its entries and transform them
                else if (value instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) value;
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        transformAttributesToString(entry.getKey());
                        transformAttributesToString(entry.getValue());
                    }
                } 
                // If the field is an array, iterate over its elements and transform them
                else if (value.getClass().isArray()) {
                    int length = java.lang.reflect.Array.getLength(value);
                    for (int i = 0; i < length; i++) {
                        Object arrayItem = java.lang.reflect.Array.get(value, i);
                        transformAttributesToString(arrayItem);
                    }
                } 
                // If the field is an object, recursively transform its attributes
                else {
                    transformAttributesToString(value);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to check if a class is a wrapper for a primitive type
    private static boolean isPrimitiveWrapper(Class<?> clazz) {
        return clazz == Boolean.class ||
               clazz == Byte.class ||
               clazz == Character.class ||
               clazz == Double.class ||
               clazz == Float.class ||
               clazz == Integer.class ||
               clazz == Long.class ||
               clazz == Short.class;
    }

    public static void main(String[] args) {
        // Exemplo de uso
        // Suponha que temos uma classe Person com alguns atributos
        class Person {
            String name;
            int age;
            Address address;

            Person(String name, int age, Address address) {
                this.name = name;
                this.age = age;
                this.address = address;
            }
        }

        class Address {
            String street;
            int number;

            Address(String street, int number) {
                this.street = street;
                this.number = number;
            }
        }

        Address address = new Address("Main Street", 123);
        Person person = new Person("John", 30, address);

        transformAttributesToString(person);

        System.out.println("Name: " + person.name); // Output: Name: John
        System.out.println("Age: " + person.age); // Output: Age: 30
        System.out.println("Street: " + person.address.street); // Output: Street: Main Street
        System.out.println("Number: " + person.address.number); // Output: Number: 123
    }
}
