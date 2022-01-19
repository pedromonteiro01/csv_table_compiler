package SecondaryGrammar;
import java.util.*;

import MainGrammar.ErrorHandling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;


public class Table {
    private List<String> columnNames = new ArrayList<>();
    private LinkedHashMap <String, ArrayList<Object>> columnContent = new LinkedHashMap<>();
    private LinkedHashMap <String, String> columnTypes = new LinkedHashMap<>();
    private String name;

    public Table(){}
    public Table(String name){
        this.name = name;
    }

    public void setName(String name){ 
        this.name = name;
    }
    public String getName(){ return this.name; }

    public void addColumn (String columnName, String type) {
        if (columnNames.contains(columnName)) {
            System.err.println("ERROR! Column name already exists!");
            System.exit(1);
        }
        columnNames.add(columnName);
        columnTypes.put(columnName, type);
        columnContent.put(columnName, new ArrayList<>());
    }

    public void addToColumn(String columnName, String content) {
        ArrayList<Object> temp = new ArrayList<>();
        String type = columnTypes.get(columnName);
        switch(type){
            case "string":
                temp = columnContent.get(columnName);
                temp.add(content);
                temp = columnContent.put(columnName, temp);
                break;
            case "int":
                Integer input = Integer.parseInt(content);
                temp = columnContent.get(columnName);
                temp.add(input);
                temp = columnContent.put(columnName, temp);
                break;
            case "boolean":
                Boolean input2 = Boolean.parseBoolean(content);
                temp = columnContent.get(columnName);
                temp.add(input2);
                temp = columnContent.put(columnName, temp);
                break;
            case "double":
                Double input3 = Double.parseDouble(content);
                temp = columnContent.get(columnName);
                temp.add(input3);
                temp = columnContent.put(columnName, temp);
                break;
            default:
                ErrorHandling.printError("Invalid type!");
                break;
        }
    } 

    protected void writeToFile(String filename) throws IOException, FileNotFoundException {
        File f = new File(filename);
        f.createNewFile();

        PrintWriter pw = new PrintWriter(f);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < columnNames.size(); i++) {
            sb.append(columnNames.get(i));
            if (i != columnNames.size() - 1) sb.append(',');
        }

        sb.append("\n");

        for (int i = 0; i < columnContent.keySet().size(); i++) {
            ArrayList<Object> temp = columnContent.get(i);

            for (int j = 0; j < temp.size(); j++) {
                sb.append(temp.get(j));

                if (j != temp.size() - 1) 
                    sb.append(',');

            if (i != columnContent.keySet().size() - 1) sb.append("\n");
            }

        }

        pw.write(sb.toString());
        pw.close();
    }

    public void checkTableFormat( String filename){
        Table t2 = ReadFileMain.WriteFile(this);
        //try{
        System.out.println(t2==null);
        //t2.writeToFile(filename);
        //}catch(IOException e) {
        // e.printStackTrace();
        // System.exit(1);
      //}
    
    }

    public void writeToConsole() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < columnNames.size(); i++) {
            sb.append(columnNames.get(i));
            if (i != columnNames.size() - 1) 
                sb.append(',');
        }
        sb.append("\n");
        int size = this.getTableSize();
        int j;
        for(int i=0; i<size; i++) {
            j=0;
            for (Map.Entry<String,ArrayList<Object>> key : columnContent.entrySet()) {
                sb.append(key.getValue().get(i));
                if(j != columnContent.size()-1){
                    sb.append(",");
                }
                j++;
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }
    public void setColumnTo(String columnName){

    }
    
    public static Table importFromFile(String filename){ 
        Table t1 = new Table();
        t1 = ReadFileMain.ImportFile(filename);
        return t1;

     }

    public LinkedHashMap<String, ArrayList<Object>> getTable() {
        return columnContent;
    }
    
    public List<String> getColumnNames() {
        return columnNames;
    }
    
    public ArrayList<Object> getColumnContent (String columnName) {
        return columnContent.get(columnName);
    }
    
    public String getTableName(){
        return this.name;
    }
    public void removeColumn(String columnName){
        this.columnContent.remove(columnName);
        this.columnNames.remove(columnName);
    }
    public int getTableSize(){
        int size = 0;
        Collection<ArrayList<Object>> lst = this.columnContent.values();
        for (ArrayList<Object> arr : lst) {
            if(arr.size()>=size) size = arr.size();
        }
        return size;
    }

    public void setContent(LinkedHashMap<String, ArrayList<Object>> content){
        this.columnContent = content;
    }
    public void setColumns(List<String> columns){
        this.columnNames = columns;
    }
    public static Table appendTable(Table t1, Table t2){ 
        Table t3 = new Table();
        LinkedHashMap<String, ArrayList<Object>> lT1 = t1.getTable();
        LinkedHashMap<String, ArrayList<Object>> lT2 = t2.getTable();
        List<String> namesT1 = t1.getColumnNames();
        List<String> namesT2 = t2.getColumnNames();
        lT1.putAll(lT2);
        t3.setContent(lT1);
        namesT1.addAll(namesT2);
        t3.setColumns(namesT1);
        return t3;
    }

    public void removeRowIndex(int index){
        for(ArrayList<Object> s : columnContent.values()){
            s.remove(index);
        }
    }
    public ArrayList<Object> getRow(int index){
        ArrayList<Object> arr = new ArrayList<>();
        for(ArrayList<Object> s : this.columnContent.values()){
            arr.add(s.get(index));
        }
        return arr;
    }
    public String getColumnType(String columnName){
        return this.columnTypes.get(columnName);
    }

    public Table filterBy(String operator, String columnName, String value){
        Table t1 = new Table();
        for(String col : this.getColumnNames()){
            t1.addColumn(col, this.getColumnType(col));
        }
        ArrayList<Object> colCont = this.getColumnContent(columnName);
        ArrayList<Object> temp = new ArrayList<>();
        String type = this.getColumnType(columnName);
        if(type=="string"){
            Object column;
            switch(operator){
            case "==":
                for(int i = 0; i< colCont.size();i++){
                    column= colCont.get(i);
                    if(column.toString().equals(value)){
                        for(String colName : this.getColumnNames()){
                                t1.addToColumn(colName, this.getColumnContent(colName).get(i).toString());
                        }
                    }
                }
                break;
            case "!=":
                for(int i = 0; i< colCont.size();i++){
                    column= colCont.get(i);
                    if(!column.toString().equals(value)){
                        for(String colName : this.getColumnNames()){
                                t1.addToColumn(colName, this.getColumnContent(colName).get(i).toString());
                        }
                    }
                }
                break;
            default:
                ErrorHandling.printError("Invalid operator!");
                break;
        }
        }else if(type=="int"){
            Object column;
            switch(operator){
                case "<":
                    for(int i = 0; i< colCont.size();i++){
                        column= colCont.get(i);
                        if(Integer.parseInt(column.toString()) < Integer.parseInt(value)){
                            for(String colName : this.getColumnNames()){
                                    t1.addToColumn(colName, this.getColumnContent(colName).get(i).toString());
                            }
                        }
                    }
                    break;
                case ">":
                for(int i = 0; i< colCont.size();i++){
                        column= colCont.get(i);
                        if(Integer.parseInt(column.toString()) > Integer.parseInt(value)){
                            for(String colName : this.getColumnNames()){
                                    t1.addToColumn(colName, this.getColumnContent(colName).get(i).toString());
                            }
                        }
                    }
                    break;
                case "==":
                    for(int i = 0; i< colCont.size();i++){
                        column= colCont.get(i);
                        if(column.toString().equals(value)){
                            for(String colName : this.getColumnNames()){
                                    t1.addToColumn(colName, this.getColumnContent(colName).get(i).toString());
                            }
                        }
                    }
                    break;
                case "!=":
                    for(int i = 0; i< colCont.size();i++){
                            column= colCont.get(i);
                            if(!column.toString().equals(value)){
                                for(String colName : this.getColumnNames()){
                                        t1.addToColumn(colName, this.getColumnContent(colName).get(i).toString());
                                }
                            }
                    }
                    break;
                default:
                    ErrorHandling.printError("Invalid operator!");
                    break;
            }
        }else if(type=="boolean"){
            Object column;
            switch(operator){
                case "==":
                    for(int i = 0; i< colCont.size();i++){
                        column= colCont.get(i);
                        if((Boolean) column == Boolean.parseBoolean(value)){
                            for(String colName : this.getColumnNames()){
                                    t1.addToColumn(colName, this.getColumnContent(colName).get(i).toString());
                            }
                        }
                    }
                    break;
                case "!=":
                    for(int i = 0; i< colCont.size();i++){
                        column= colCont.get(i);
                        if((Boolean) column != Boolean.parseBoolean(value)){
                            for(String colName : this.getColumnNames()){
                                    t1.addToColumn(colName, this.getColumnContent(colName).get(i).toString());
                            }
                        }
                    }
                    break;
                default:
                    ErrorHandling.printError("Invalid operator!");
                    break;
            }
        }
        else if(type=="double"){
             Object column;
            switch(operator){
                case "<":
                    for(int i = 0; i< colCont.size();i++){
                        column= colCont.get(i);
                        if(Double.parseDouble(column.toString()) < Double.parseDouble(value)){
                            for(String colName : this.getColumnNames()){
                                    t1.addToColumn(colName, this.getColumnContent(colName).get(i).toString());
                            }
                        }
                    }
                    break;
                case ">":
                for(int i = 0; i< colCont.size();i++){
                        column= colCont.get(i);
                        if(Double.parseDouble(column.toString()) > Double.parseDouble(value)){
                            for(String colName : this.getColumnNames()){
                                    t1.addToColumn(colName, this.getColumnContent(colName).get(i).toString());
                            }
                        }
                    }
                    break;
                case "==":
                    for(int i = 0; i< colCont.size();i++){
                        column= colCont.get(i);
                        if(column.toString().equals(value)){
                            for(String colName : this.getColumnNames()){
                                    t1.addToColumn(colName, this.getColumnContent(colName).get(i).toString());
                            }
                        }
                    }
                    break;
                case "!=":
                    for(int i = 0; i< colCont.size();i++){
                            column= colCont.get(i);
                            if(!column.toString().equals(value)){
                                for(String colName : this.getColumnNames()){
                                        t1.addToColumn(colName, this.getColumnContent(colName).get(i).toString());
                                }
                            }
                    }
                    break;
                default:
                    ErrorHandling.printError("Invalid operator!");
                    break;
            }
        }
        return t1;
        
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < columnNames.size(); i++) {
            sb.append(columnNames.get(i));
            if (i != columnNames.size() - 1) 
                sb.append(',');
        }
        sb.append("\n");
        int size = this.getTableSize();
        int j;
        for(int i=0; i<size; i++) {
            j=0;
            for (Map.Entry<String,ArrayList<Object>> key : columnContent.entrySet()) {
                sb.append(key.getValue().get(i));
                if(j != size-2){
                    sb.append(",");
                }
                j++;
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}