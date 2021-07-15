package data;

import data.Cell;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Neighbours {
    private final List<Cell> neighboursCell;
    private final List<Integer> neighbourId;
    private Random random;

    // Constructor
    public Neighbours() {
        this.neighboursCell = new LinkedList<>();
        this.neighbourId = new LinkedList<>();
        random = new Random();
    }

    // add neighbour if its state equal 1 - one id = one neighbour
    public void addCell(Cell cell) {
        if(cell.getState()==1) {
            int id = check(cell.getId());
            if (id == -1) {
                neighboursCell.add(cell);
                neighbourId.add(1);
            } else {
                int value = neighbourId.get(id);
                neighbourId.set(id, value + 1);
            }
        }
    }

    // clear list of neighbours
    public void clear(){
        if (neighboursCell.size()>0){
            neighboursCell.clear();
            neighbourId.clear();
        }
    }

    // return size of neighbour which state equals 1
    public int size(){
        return neighboursCell.size();
    }

    // find neighbour which is the most
    public Cell findMax(){
        if(neighbourId.size()>0) {
            int max = 0, id = 0;
            for (int i = 0; i < neighbourId.size(); i++) {
                if (max > neighbourId.get(i)) {
                    max = neighbourId.get(i);
                    id = i;
                }
            }
            return neighboursCell.get(id);
        }
        else return null;
    }

    // check if is cell witch this id in neighboursCell list
    private int check(int id) {
        int c = -1;
        for (int i =0;i<neighboursCell.size();i++){
            if(neighboursCell.get(i).getId() == id) {
                c = i;
                break;
            }
        }
        return c;
    }

    // return amount of cell with this id
    public int amount(int id){
        int i = check(id);
        if(i == -1)
            return 0;
        else
            return neighbourId.get(i);
    }

    // return random neighbour (Cell)
    public Cell randomCell(){
        int rand = random.nextInt(this.neighboursCell.size());
        return this.neighboursCell.get(rand);
    }
}
