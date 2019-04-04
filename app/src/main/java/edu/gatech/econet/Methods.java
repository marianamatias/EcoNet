package edu.gatech.econet;

public class Methods {
    //For string
    public static String[] deleteString(String [] inputList,int index){
        String [] tmpList = new String[] {};
        if (inputList!=tmpList){
            int n = inputList.length - 1;
            for (int i=0 ; i<n+1 ; i++){
                if (i!=index){
                    tmpList=increaseArray(tmpList,inputList[i]);
                }
            }
            return tmpList;
        }
        return tmpList;
    }
    public static String[] increaseArray(String[] input, String newElem){
        String [] nullList = new String []{};
        if (input!=nullList) {
            int i = input.length;
            String[] newArray = new String[i + 1];
            for (int cnt = 0; cnt < i; cnt++) {
                newArray[cnt] = input[cnt];
            }
            newArray[i] = newElem;
            return newArray;
        }
        else {
            String [] returnList = new String[] {newElem};
            return returnList;
        }
    }
    //For boolean
    public static boolean[] deleteStringBool(boolean [] inputList,int index){
        boolean [] tmpList = new boolean[] {};
        if (inputList!=tmpList){
            int n = inputList.length - 1;
            for (int i=0 ; i<n+1 ; i++){
                if (i!=index){
                    tmpList=increaseArrayBool(tmpList,inputList[i]);
                }
            }
            return tmpList;
        }
        return tmpList;
    }
    public static boolean[] increaseArrayBool(boolean[] input, boolean newElem){
        boolean [] nullList = new boolean[]{};
        if (input!=nullList) {
            int i = input.length;
            boolean[] newArray = new boolean[i + 1];
            for (int cnt = 0; cnt < i; cnt++) {
                newArray[cnt] = input[cnt];
            }
            newArray[i] = newElem;
            return newArray;
        }
        else {
            boolean [] returnList = new boolean[] {newElem};
            return returnList;
        }
    }
    public static int find(String[] inputList, String target){
        for (int i = 0; i < inputList.length; i++) {
            if (inputList[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
    public static boolean isInArray(String[] inputList, String target){
        int found= 0;
        for (int i =0; i< inputList.length;i++){
            if (inputList[i].equals(target)){
                found=1;
            }
        }
        if(found==1){
            return true;
        }
        else{ return false;}
    }

    public static String[] removeTheElement(String[] arr, String seek){
        if (arr == null) {
            return arr;
        }
        String[] anotherArray = new String[arr.length - 1];
        for (int i = 0, k = 0; i < arr.length; i++) {
            if (arr[i].equals(seek)) {
                continue;
            }
            anotherArray[k++] = arr[i];
        }
        return anotherArray;
    }

}
