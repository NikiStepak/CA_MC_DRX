package data;

public class BoundaryCondition {
    private final String[] boundaryCondition_tab = {"ABSORBING", "PERIODIC"};

    // return true if condition equals "PERIODIC" else return false
    public boolean getCondition(String condition){
        return condition.equals(boundaryCondition_tab[1]);
    }

    // return tab
    public String[] getBoundaryCondition_tab() {
        return boundaryCondition_tab;
    }
}