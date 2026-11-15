import java.time.DayOfWeek;
import java.util.Set;

/**
 * A dietitian. Dietitians review a patient's nutrition and write meal plans;
 * they do not prescribe medication, so this class does not implement
 * {@link Prescriber}.
 *
 * <p>Extra data: the nutrition focus area. Extra behaviour: designing a meal
 * plan.</p>
 */
public class Dietitian extends HealthProfessional {

    /** Nutrition focus area, for example "Child Nutrition". */
    private final String nutritionFocus;

    /**
     * Creates a dietitian with an explicit weekly roster.
     *
     * @param id             numeric identifier, greater than zero
     * @param name           name of the dietitian, not blank
     * @param workingDays    days worked every week, not empty
     * @param nutritionFocus focus area of the dietitian, not blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Dietitian(int id, String name, Set<DayOfWeek> workingDays, String nutritionFocus) {
        super(id, name, workingDays);
        this.nutritionFocus = requireFocus(nutritionFocus);
    }

    /**
     * Creates a dietitian who works the default Monday-to-Friday week.
     *
     * @param id             numeric identifier, greater than zero
     * @param name           name of the dietitian, not blank
     * @param nutritionFocus focus area of the dietitian, not blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Dietitian(int id, String name, String nutritionFocus) {
        super(id, name);
        this.nutritionFocus = requireFocus(nutritionFocus);
    }

    /**
     * Validates the nutrition focus area.
     *
     * @param focus focus area supplied by the caller
     * @return the trimmed focus area
     * @throws IllegalArgumentException if the focus area is null or blank
     */
    private static String requireFocus(String focus) {
        if (focus == null || focus.trim().isEmpty()) {
            throw new IllegalArgumentException("Nutrition focus area must not be blank.");
        }
        return focus.trim();
    }

    /**
     * Returns the nutrition focus area of this dietitian.
     *
     * @return the nutrition focus area of this dietitian
     */
    public String getNutritionFocus() {
        return nutritionFocus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRoleDescription() {
        return "Dietitian";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String describeAdditionalDetails() {
        return "\n" + detailLine("Focus area", nutritionFocus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String performPrimaryDuty(Patient patient) {
        return designMealPlan(patient);
    }

    /**
     * Simulates writing a meal plan for the patient.
     *
     * @param patient patient who receives the plan
     * @return readable description of the plan
     * @throws IllegalArgumentException if the patient is null
     */
    public String designMealPlan(Patient patient) {
        requirePatient(patient);
        return getName() + " designed a " + nutritionFocus + " meal plan for " + patient.getName() + ".";
    }
}
