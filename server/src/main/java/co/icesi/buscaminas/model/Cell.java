package co.icesi.buscaminas.model;

public class Cell {

    private boolean isLandMine;
    private int value;
    private boolean hide;
    private boolean isMarked;
    private boolean showAll;

    public Cell() {
        this.isLandMine = false;
        this.value = 0;
        this.hide = true;
        this.isMarked = false;
        this.showAll = false;
    }

    public Cell(Cell other) {
        this.isLandMine = other.isLandMine;
        this.value = other.value;
        this.hide = other.hide;
        this.isMarked = other.isMarked;
        this.showAll = other.showAll;
    }

    public boolean isLandMine() {
        return isLandMine;
    }

    public void setLandMine(boolean landMine) {
        isLandMine = landMine;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }
}
