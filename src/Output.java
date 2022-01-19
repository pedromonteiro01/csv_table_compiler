import java.lang.Math;
import java.util.*;
import Files.*;
import SecondaryGrammar.*;

public class Output {
    public static void main(String[] args) {
      Table biblioteca = new Table("biblioteca");
      biblioteca.addColumn("LIVROS", "string");
      biblioteca.addColumn("TIPOS", "string");
      biblioteca.addColumn("EXEMPLARES", "int");
      biblioteca.addColumn("AUTOR", "string");
      String[] v2 = "'Red Queen', 'Ficçao cientifica', 12, 'Victoria Aveyard'".split(",");
      int v1 = 0;
      for(String s : v2) {
         biblioteca.addToColumn(biblioteca.getColumnNames().get(v1), s.trim());
         v1++;
      }
      String[] v4 = "'Os Maias', 'Romance/Ficçao', 18, 'Eça de Queirós'".split(",");
      int v3 = 0;
      for(String s : v4) {
         biblioteca.addToColumn(biblioteca.getColumnNames().get(v3), s.trim());
         v3++;
      }
      String[] v6 = "'Segurança em Redes Informáticas', 'Educação', 6, 'André Zúquete'".split(",");
      int v5 = 0;
      for(String s : v6) {
         biblioteca.addToColumn(biblioteca.getColumnNames().get(v5), s.trim());
         v5++;
      }
      String[] v8 = "'Tales of Fire and Ice', 'Fantasia Épica', 100, 'George R. R. Martin'".split(",");
      int v7 = 0;
      for(String s : v8) {
         biblioteca.addToColumn(biblioteca.getColumnNames().get(v7), s.trim());
         v7++;
      }
      String[] v10 = "'Uma Aventura em Engenharia Informática', 'Educação', 1,'Grupo-09'".split(",");
      int v9 = 0;
      for(String s : v10) {
         biblioteca.addToColumn(biblioteca.getColumnNames().get(v9), s.trim());
         v9++;
      }
      String[] v12 = "'Os Lusíadas', 'Poesia', 22, 'Luíz Vaz de Camões'".split(",");
      int v11 = 0;
      for(String s : v12) {
         biblioteca.addToColumn(biblioteca.getColumnNames().get(v11), s.trim());
         v11++;
      }
      Table v13 = biblioteca;
      v13.writeToConsole();
      Scanner sc = new Scanner(System.in);

      System.out.print("Introduza a linha [0-6]: ");
      Integer v14 = sc.nextInt();
      Integer indice = v14;
    }
}
