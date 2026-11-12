import java.util.ArrayList;

/**
 * Dietitian subclass of HealthProfessional
 */
public class Dietitian extends HealthProfessional {
    private final String focusArea;

    public Dietitian(int id, String name, ArrayList<String> availableDays, String focusArea) {
        super(id, name, availableDays);
        this.focusArea = focusArea;
    }

    public String getFocusArea() {
        return focusArea;
    }

    @Override
    public String getRoleDescription() {
        return "Dietitian";
    }

    @Override
    public String toString() {
        return super.toString() + "\nNutrition focus area: " + focusArea;
    }
}
