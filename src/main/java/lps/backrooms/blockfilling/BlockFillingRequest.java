package lps.backrooms.blockfilling;

public class BlockFillingRequest {

    private Integer[] pos1 = new Integer[3];
    private Integer[] pos2 = new Integer[3];
    private String oldBlocks;
    private String newBlocks;

    public Integer[] getPos1() {
        return pos1;
    }

    public Integer[] getPos2() {
        return pos2;
    }

    public String getOldBlocks() {
        return oldBlocks;
    }

    public String getNewBlocks() {
        return newBlocks;
    }

    public void init(Integer[] pos1, Integer[] pos2, String oldBlocks, String newBlocks){
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.oldBlocks = oldBlocks;
        this.newBlocks = newBlocks;
    }

}
