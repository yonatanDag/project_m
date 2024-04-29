package project.example.Model;

import java.util.ArrayList;

public class SpecializationService {
    // ArrayList of all of the Technicians and their Specializations
    private ArrayList<SpecializationTechnician> stList;

    public SpecializationService(ArrayList<SpecializationTechnician> stList) {
        this.stList = stList;
    }

    // function that return true if the Technician tech is specialize in the Specialization spec
    public boolean isTechSpec(Technician tech, Specialization spec) {
        int startIndex = findFirstIndex(tech);
        if (startIndex == -1) {
            return false;
        }

        while (startIndex < stList.size() && stList.get(startIndex).getTech().getIdT() == tech.getIdT()) {
            SpecializationTechnician currentSpecTech = stList.get(startIndex);
            if (currentSpecTech.getType().getIdS() == spec.getIdS()) {
                return true;
            }
            startIndex++;
        }
        return false;
    }

    // function that return true if the Technicians tech1, tech2 are specialize in atleast 1 same Specialization
    public boolean isTechsSuit(Technician tech1, Technician tech2) {
        int startIndexTech1 = findFirstIndex(tech1);
        int startIndexTech2 = findFirstIndex(tech2);

        if (startIndexTech1 == -1 || startIndexTech2 == -1) {
            return false;
        }

        while (startIndexTech1 < stList.size() && stList.get(startIndexTech1).getTech().getIdT() == tech1.getIdT() &&
                startIndexTech2 < stList.size() && stList.get(startIndexTech2).getTech().getIdT() == tech2.getIdT()) {

            int specId1 = stList.get(startIndexTech1).getType().getIdS();
            int specId2 = stList.get(startIndexTech2).getType().getIdS();

            if (specId1 == specId2) {
                return true;
            } else if (specId1 < specId2) {
                startIndexTech1++;
            } else {
                startIndexTech2++;
            }
        }
        return false;
    }

    // function that find the start index of a technician's specializations in the sorted list
    private int findFirstIndex(Technician tech) {
        int low = 0;
        int high = stList.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            SpecializationTechnician midVal = stList.get(mid);
            if (midVal.getTech().getIdT() < tech.getIdT()) {
                low = mid + 1;
            } else if (midVal.getTech().getIdT() > tech.getIdT()) {
                high = mid - 1;
            } else {
                while (mid > low && stList.get(mid - 1).getTech().getIdT() == tech.getIdT()) {
                    mid--;
                }
                return mid;
            }
        }
        return -1;
    }

    // function that get Technician tech and Specialization spec and return the level of this Technician in this Specialization
    // the level is 1 if in this Specialization his rating is higher than all of his ratings in the other Specializations
    public int level(Technician tech, Specialization spec) {
        int startIndex = findFirstIndex(tech);
        if (startIndex == -1) {
            return -1; // Technician not found
        }

        double highestRating = Double.MIN_VALUE;
        double specRating = -1; // the rating in the specified specialization
        boolean isSpecializedInSpec = false;

        for (int i = startIndex; i < stList.size() && stList.get(i).getTech().getIdT() == tech.getIdT(); i++) {
            SpecializationTechnician current = stList.get(i);
            double currentRating = current.getRating();

            if (current.getType().getIdS() == spec.getIdS()) {
                isSpecializedInSpec = true;
                specRating = currentRating;
            }

            if (currentRating > highestRating) {
                highestRating = currentRating;
            }
        }   

        if (isSpecializedInSpec && specRating >= highestRating) {
            return 1; // this specialization has the highest rating for the technician
        }

        return 0; // this specialization does not have the highest rating for the technician
    }
}
