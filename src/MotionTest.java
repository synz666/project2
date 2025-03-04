import java.io.*;
import java.util.*;

/**
 * Клас MotionData зберігає параметри та обчислює траєкторію руху.
 * Реалізує Serializable для можливості серіалізації.
 */
class MotionData implements Serializable {
    private static final long serialVersionUID = 1L;
    private double v0, alpha;
    private static final double G = 9.81;
    private transient List<int[]> trajectory = new ArrayList<>();

    public MotionData(double v0, double alpha) {
        this.v0 = v0;
        this.alpha = Math.toRadians(alpha);
    }

    /**
     * Обчислює траєкторію руху.
     */
    public void calculate(double totalTime, double step) {
        trajectory = new ArrayList<>();
        for (double t = 0; t <= totalTime; t += step) {
            int x = (int) (v0 * Math.cos(alpha) * t);
            int y = (int) (v0 * Math.sin(alpha) * t - (G * t * t) / 2);
            if (y < 0) break;
            trajectory.add(new int[]{x, y});
        }
    }

    /**
     * Повертає список точок траєкторії у 16-річному форматі.
     */
    public void printHexTrajectory() {
        trajectory.stream()
            .map(p -> String.format("x: %s, y: %s", Integer.toHexString(p[0]), Integer.toHexString(p[1])))
            .forEach(System.out::println);
    }
}

/**
 * Клас Serializer відповідає за збереження та завантаження MotionData.
 */
class Serializer {
    public static void save(MotionData data, String file) throws IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(data);
        }
    }

    public static MotionData load(String file) throws IOException, ClassNotFoundException {
        try (var in = new ObjectInputStream(new FileInputStream(file))) {
            return (MotionData) in.readObject();
        }
    }
}

/**
 * Головний клас для демонстрації роботи програми.
 */
public class MotionTest {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Введіть v0 і кут α через пробіл: ");
            double v0 = scanner.nextDouble(), alpha = scanner.nextDouble();

            MotionData data = new MotionData(v0, alpha);
            data.calculate(2, 0.1);
            System.out.println("Траєкторія (HEX):");
            data.printHexTrajectory();

            String filename = "motion_data.ser";
            try {
                Serializer.save(data, filename);
                System.out.println("Дані збережено у файл.");

                MotionData loaded = Serializer.load(filename);
                System.out.println("Об'єкт відновлено. Повторний розрахунок:");
                loaded.calculate(2, 0.1);
                loaded.printHexTrajectory();
            } catch (Exception e) {
                System.err.println("Помилка: " + e.getMessage());
            }
        }
    }
}
