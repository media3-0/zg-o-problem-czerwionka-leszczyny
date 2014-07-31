package com.media30.zglosproblem.mobileapp;

final public class Categories {
    public static String [] categories = new String[] {
            "Uszkodzenie, dewastacja",
            "Drogi",
            "Komunikacja",
            "Wodno-kanalizacyjne",
            "Lokalowe",
            "Zieleń",
            "Zwierzęta",
            "Śmieci",
            "Odśnieżanie",
            "Straż miejska",
            "Inne"
    };

    public static int [] categoriesResources = new int[] {
            R.drawable.dewastacja,
            R.drawable.drogi,
            R.drawable.komunikacja,
            R.drawable.wodno_kanalizacyjne,
            R.drawable.lokalowe,
            R.drawable.zielen,
            R.drawable.zwierzeta,
            R.drawable.smieci,
            R.drawable.odsniezanie,
            R.drawable.lokalowe //fixme : ikonka dla straży miejskiej!
    };

    public static String [][] subcategories = new String[][] {
            {//Uszkodzenie, dewastacja
                "graffiti",
                "budynku użyteczności publicznej",
                "zdewastowany plac zabaw",
                "zdewastowana ławka",
                "zdewastowany kosz na śmieci",
                "pojemnik na kwiaty",
                "zdewastowany przystanek",
                "uszkodzona latarnia",
                "inne"
            }, {//Drogi
                "uszkodzenie barierki, słupka, znaku drogowego",
                "uszkodzona latarnia",
                "uszkodzenie sygnalizacji świetlne",
                "uszkodzona nawierzchnia",
                "inne"
            }, {//Komunikacja
                "tłok w pojazdach",
                "niepunktualne autobusy",
                "zmiany w kursowaniu autobusów",
                "zły stan techniczny pojazdu",
                "inne"
            }, {//Wodno-kanalizacyjne
                "brak odpływu ścieków",
                "zła jakość wody",
                "brak wody",
                "gryzonie w kanalizacji",
                "problem z odbiorem nieczystości płynnych",
                "uszkodzenie lub brak pokrywy studzienki/studni kanalizacyjnej",
                "uszkodzenie lub brak kratki ściekowej",
                "nieprzyjemny zapach z kanalizacji",
                "uszkodzony wodomierz",
                "inne"
            }, {//Lokalowe
                "awaria ogrzewania w lokalu komunalnym",
                "przeciekający dach",
                "odpadający tynk",
                "inne"
            }, {//Zieleń
                "powalony konar",
                "złamane/suche drzewo",
                "zdewastowana zieleń",
                "wycięte drzewa",
                "uszkodzony Pomnik Przyrody",
                "inne"
            }, {//Zwierzęta
                "gryzonie w miejscu publicznym",
                "dzikie zwierzęta",
                "martwe zwierzęta",
                "inne"
            }, {//Śmieci
                "zaleganie nieczystości w miejscach publicznych",
                "przepełniony śmietnik",
                "dzikie wysypisko",
                "inne"
            }, {//Odśnieżanie
                "sople lodu na budynkach",
                "śnieg na dachach budynków",
                "odśnieżanie ulic i chodników",
                "odśnieżanie przystanków",
                "inne"
            }, {//Straż miejska
                "Spożywanie alkoholu w miejscu publicznym",
                "Zakłócanie ciszy",
                "Częste bójki",
                "inne"
            }, {//pusta tablica = "inne"
            }
    };

    public static String getCategory(int cat){
        return categories[cat];
    }

    public static String getSubcategory(int cat, int sub){
        return subcategories[cat][sub];
    }
}
