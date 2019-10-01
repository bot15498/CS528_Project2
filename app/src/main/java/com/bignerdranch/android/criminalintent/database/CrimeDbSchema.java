package com.bignerdranch.android.criminalintent.database;

public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }

    public static final class CrimeImageTable {
        public static final String NAME = "images";
        public static final class Cols {
            public static final String CRIMEID = "crimeID";
            public static final String FILEPATH = "filePath";
        }
    }
}
