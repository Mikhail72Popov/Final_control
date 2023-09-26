import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class ListAnimals implements AutoCloseable {

    private List<Animal> animals = new ArrayList<>();

    private static Counter counter = new Counter();

    public void addNewAnimal(Animal animal) {
        animals.add(animal);
        counter.add();
    }

    public void teachCommand(Animal animal, String command) {
        animal.setCommand(command);

        // Запись данных в базу данных
        try (FileWriter writer = new FileWriter("DataBase.csv", true)) {
            String animalType = getAnimalType(animal);
            String animalName = animal.getName();
            String line = animalType + "," + animalName + "," + command + "\n";
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAnimalType(Animal animal) {
        if (animal instanceof Dogs) {
            return "Собаки";
        } else if (animal instanceof Cats) {
            return "Кошки";
        } else if (animal instanceof Hamsters) {
            return "Хомяки";
        } else if (animal instanceof Horses) {
            return "Лошади";
        } else if (animal instanceof Camels) {
            return "Верблюды";
        } else if (animal instanceof Donkeys) {
            return "Ослы";
        }
        return "";
    }

    public List<String> getCommands(Animal animal) {
        List<String> commands = new ArrayList<>();
        commands.add(animal.getCommand());
        return commands;
    }

    public void readDatabase() {       
        File databaseFile = new File("DataBase.csv"); // Создание файла базы данных
        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(databaseFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    String animalName = data[0];
                    String command = data[1];
                    Animal animal = animals.stream().filter(a -> a.getName().equals(animalName)).findFirst().orElse(null);
                    if (animal != null) {
                        animal.setCommand(command);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        try (ListAnimals petRegistry = new ListAnimals()) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("1. Добавить животное");
                System.out.println("2. Обучить команде");
                System.out.println("3. Добавить команду");
                System.out.println("4. Выход");
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        System.out.println("Введи имя животного: ");
                        String name = scanner.nextLine();
                        System.out.println("1-собаки, 2-кошки, 3-хомяки, 4-лошади, 5-верблюды, 6-ослы");
                        System.out.println("Введи цифру, соответствующую типу животного: ");
                        
                        Scanner type = new Scanner(System.in);
                        Animal animal;
                        switch (type.nextInt()) {
                            case 1:
                                animal = new Dogs(name);
                                break;
                            case 2:
                                animal = new Cats(name);
                                break;
                            case 3:
                                animal = new Hamsters(name);
                                break;
                            case 4:
                                animal = new Horses(name);
                                break;
                            case 5:
                                animal = new Camels(name);
                                break;
                            case 6:
                                animal = new Donkeys(name);
                                break;
                            default:
                                throw new IllegalStateException("Не соответствие: " + type);
                        }
                        petRegistry.addNewAnimal(animal);
                        break;
                    case 2:
                        System.out.println("Введи имя животного: ");
                        String animalName = scanner.nextLine();
                        Animal foundAnimal = petRegistry.animals.stream()
                                .filter(a -> a.getName().equals(animalName))
                                .findFirst()
                                .orElse(null);
                        if (foundAnimal == null) {
                            System.out.println("Животное отсутствует");
                            break;
                        }
                        System.out.println("Введи команду: ");
                        String command = scanner.nextLine();
                        petRegistry.teachCommand(foundAnimal, command);
                        break;
                    case 3:
                        System.out.println("Введи имя жвотного: ");
                        String aName = scanner.nextLine();
                        Animal fAnimal = petRegistry.animals.stream()
                                .filter(a -> a.getName().equals(aName))
                                .findFirst()
                                .orElse(null);
                        if (fAnimal == null) {
                            System.out.println("Животное отсутствует");
                            break;
                        }
                        List<String> commands = petRegistry.getCommands(fAnimal);
                        for (String cmd : commands) {
                            System.out.println(cmd);
                        }
                        break;
                    case 4:
                        return;
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void close() throws Exception {
        if (counter.getCount() == 0) {
            throw new Exception("Counter was not used in try-with-resources block");
        } else {
            counter.resetCount();
        }
    }

}

class Counter {

    private int count;

    public void add() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public void resetCount() {
        count = 0;
    }

}
