package com.miniProjet.projet.respo;

public class UsersSaveJson {

    private int NumberOfRecords;
    private int SuccessfullyImported;
    private int NotImported;


    public int getNumberOfRecords() {
        return NumberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        NumberOfRecords = numberOfRecords;
    }

    public int getSuccessfullyImported() {
        return SuccessfullyImported;
    }

    public void setSuccessfullyImported(int successfullyImported) {
        SuccessfullyImported = successfullyImported;
    }

    public int getNotImported() {
        return NotImported;
    }

    public void setNotImported(int notImported) {
        NotImported = notImported;
    }
}
