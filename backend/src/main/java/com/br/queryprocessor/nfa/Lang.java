package com.br.queryprocessor.nfa;

public class Lang {
    private NFA nfa;
    char[] sigma = ".,/*-_+abcdefghijklmnopqrstuvxywz*()\"'*1234567890".toCharArray();

    public Lang(String[] tables) {
        State firstState = this.getFirstState(tables);

        this.nfa = new NFA(firstState);
    }

    public State getFirstState(String[] tables) {
        State qInitial = new State("qInitial");

        State selectS = new State("selectS");
        State selectE1 = new State("selectE1");
        State selectL = new State("selectL");
        State selectE2 = new State("selectE2");
        State selectC = new State("selectC");
        State selectT = new State("selectT");
        State select_ = new State("select_");

        qInitial.addTransition(selectS, 's');
        selectS.addTransition(selectE1, 'e');
        selectE1.addTransition(selectL, 'l');
        selectL.addTransition(selectE2, 'e');
        selectE2.addTransition(selectC, 'c');
        selectC.addTransition(selectT, 't');
        selectT.addTransition(select_, ' ');

        State columnSelect = new State("columnSelect");
        State columnSelectComma = new State("columnSelectComma");
        State columnSelect_ = new State("columnSelect_");

        columnSelect.addTransition(columnSelectComma, ',');
        columnSelect.addTransition(columnSelect_, ' ');
        columnSelectComma.addTransition(columnSelect_, ' ');

        for (char c : sigma) {
            select_.addTransition(columnSelect, c);
            columnSelect.addTransition(columnSelect, c);
            columnSelect_.addTransition(columnSelect, c);
        }

        State fromF = new State("fromF");
        State fromR = new State("fromR");
        State fromO = new State("fromO");
        State fromM = new State("fromM");
        State from_ = new State("from_");

        columnSelect_.addTransition(fromF, 'f');
        fromF.addTransition(fromR, 'r');
        fromR.addTransition(fromO, 'o');
        fromO.addTransition(fromM, 'm');
        fromM.addTransition(from_, ' ');

        State table_ = new State("table_");

        for (String s : tables) {
            char[] table = s.toCharArray();

            State tableChar = new State(s + table[0]);
            from_.addTransition(tableChar, table[0]);

            for (int j = 1; j < table.length; j++) {
                State tableCharTo = new State(s + table[j]);
                tableChar.addTransition(tableCharTo, table[j]);
                tableChar = tableCharTo;

                if (j == table.length - 1) {
                    tableChar.addTransition(table_, ' ');
                    tableChar.setFinal();
                }
            }
        }

        State orderByO = new State("orderByO");
        State orderByR1 = new State("orderByR1");
        State orderByD = new State("orderByD");
        State orderByE = new State("orderByE");
        State orderByR2 = new State("orderByR2");
        State orderBy_1 = new State("orderBy_1");
        State orderByB = new State("orderByB");
        State orderByY = new State("orderByY");
        State orderBy_2 = new State("orderBy_2");

        State columnOrderBy = new State("columnOrderBy");
        State columnOrderBy_ = new State("columnOrderBy_");
        State columnOrderByVirgula = new State("columnOrderByVirgula");
        State columnOrderByVirgula_ = new State("columnOrderByVirgula_");

        table_.addTransition(orderByO, 'o');
        orderByO.addTransition(orderByR1, 'r');
        orderByR1.addTransition(orderByD, 'd');
        orderByD.addTransition(orderByE, 'e');
        orderByE.addTransition(orderByR2, 'r');
        orderByR2.addTransition(orderBy_1, ' ');
        orderBy_1.addTransition(orderByB, 'b');
        orderByB.addTransition(orderByY, 'y');
        orderByY.addTransition(orderBy_2, ' ');

        for (char c : sigma) {
            orderBy_2.addTransition(columnOrderBy, c);
            columnOrderByVirgula_.addTransition(columnOrderBy, c);
            columnOrderBy.addTransition(columnOrderBy, c);
        }
        columnOrderBy.setFinal();
        columnOrderBy.addTransition(columnOrderBy_, ' ');
        columnOrderBy.addTransition(columnOrderByVirgula, ',');
        columnOrderByVirgula.addTransition(columnOrderByVirgula_, ' ');

        State ascA = new State("ascA");
        State ascS = new State("ascS");
        State ascC = new State("ascC");

        columnOrderBy_.addTransition(ascA, 'a');
        ascA.addTransition(ascS, 's');
        ascS.addTransition(ascC, 'c');
        ascC.setFinal();
        ascC.addTransition(columnOrderByVirgula, ',');


        State descD = new State("descD");
        State descE = new State("descE");
        State descS = new State("descS");
        State descC = new State("descC");

        columnOrderBy_.addTransition(descD, 'd');
        descD.addTransition(descE, 'e');
        descE.addTransition(descS, 's');
        descS.addTransition(descC, 'c');
        descC.setFinal();
        descC.addTransition(columnOrderByVirgula, ',');

        State whereW = new State("whereW");
        State whereH = new State("whereH");
        State whereE1 = new State("whereE1");
        State whereR = new State("whereR");
        State whereE2 = new State("whereE2");
        State where_ = new State("where_");

        State columnWhere = new State("columnWhere");
        State columnWhere_ = new State("columnWhere_");

        table_.addTransition(whereW, 'w');
        whereW.addTransition(whereH, 'h');
        whereH.addTransition(whereE1, 'e');
        whereE1.addTransition(whereR, 'r');
        whereR.addTransition(whereE2, 'e');
        whereE2.addTransition(where_, ' ');

        for (char c : sigma) {
            where_.addTransition(columnWhere, c);
            columnWhere.addTransition(columnWhere, c);
        }

        columnWhere.addTransition(columnWhere_, ' ');

        State greater = new State(">");
        State less = new State("<");

        State greaterOrEqual1 = new State(">=1");
        State greaterOrEqual2 = new State(">=2");

        State lessOrEqual1 = new State("<=1");
        State lessOrEqual2 = new State("<=2");

        State equal = new State( "=");

        State notEqual1 = new State("<>1");
        State notEqual2 = new State("<>2");

        State likeL = new State("likeL");
        State likeI = new State("likeI");
        State likeK = new State("likeK");
        State likeE = new State("likeE");

        State inI = new State("inI");
        State inN = new State("inN");

        State andA = new State("andA");
        State andN = new State("andN");
        State andD = new State("andD");
        State and_ = new State("and_");

        State orO = new State("orO");
        State orR = new State("orR");
        State or_ = new State("or_");

        State notN = new State("notN");
        State notO = new State("notO");
        State notT = new State("notT");
        State not_ = new State("not_");

        State op_ = new State("op_");

        State columnWhere2 = new State("columnWhere2");
        State columnWhere_2 = new State("columnWhere_2");

        columnWhere_.addTransition(greater, '>');
        columnWhere_.addTransition(less, '<');

        columnWhere_.addTransition(greaterOrEqual1, '>');
        greaterOrEqual1.addTransition(greaterOrEqual2, '=');

        columnWhere_.addTransition(lessOrEqual1, '<');
        lessOrEqual1.addTransition(lessOrEqual2, '=');

        columnWhere_.addTransition(equal, '=');

        columnWhere_.addTransition(notEqual1, '<');
        notEqual1.addTransition(notEqual2, '>');

        columnWhere_.addTransition(likeL, 'l');
        likeL.addTransition(likeI, 'i');
        likeI.addTransition(likeK, 'k');
        likeK.addTransition(likeE, 'e');

        columnWhere_.addTransition(inI, 'i');
        inI.addTransition(inN, 'n');

        greater.addTransition(op_, ' ');
        less.addTransition(op_, ' ');
        greaterOrEqual2.addTransition(op_, ' ');
        lessOrEqual2.addTransition(op_, ' ');
        equal.addTransition(op_, ' ');
        notEqual2.addTransition(op_, ' ');
        likeE.addTransition(op_, ' ');
        inN.addTransition(op_, ' ');

        for (char c : sigma) {
            op_.addTransition(columnWhere2, c);
            columnWhere2.addTransition(columnWhere2, c);
        }

        columnWhere2.setFinal();
        columnWhere2.addTransition(columnWhere_2, ' ');
        columnWhere_2.addTransition(orderByO, 'o');
        columnWhere_2.addTransition(whereW, 'w');

        where_.addTransition(notN, 'n');
        notN.addTransition(notO, 'o');
        notO.addTransition(notT, 't');
        notT.addTransition(not_, ' ');

        for (char c : sigma) {
            not_.addTransition(columnWhere, c);
            columnWhere.addTransition(columnWhere, c);
        }

        columnWhere_2.addTransition(andA, 'a');
        andA.addTransition(andN, 'n');
        andN.addTransition(andD, 'd');
        andD.addTransition(and_, ' ');

        columnWhere_2.addTransition(orO, 'o');
        orO.addTransition(orR, 'r');
        orR.addTransition(or_, ' ');

        and_.addTransition(notN, 'n');
        or_.addTransition(notN, 'n');

        for (char c : sigma) {
            and_.addTransition(columnWhere, c);
            or_.addTransition(columnWhere, c);
            columnWhere.addTransition(columnWhere, c);
        }

        State joinJ = new State("joinJ");
        State joinO = new State("joinO");
        State joinI = new State("joinI");
        State joinN = new State("joinN");
        State join_ = new State("join_");

        State joinTable = new State("joinTable");

        table_.addTransition(joinJ, 'j');
        joinJ.addTransition(joinO, 'o');
        joinO.addTransition(joinI, 'i');
        joinI.addTransition(joinN, 'n');
        joinN.addTransition(join_, ' ');

        for (String s : tables) {
            char[] table = s.toCharArray();

            State tableChar = new State(s + table[0]);
            join_.addTransition(tableChar, table[0]);

            for (int j = 1; j < table.length; j++) {
                State tableCharTo = new State(s + table[j]);
                tableChar.addTransition(tableCharTo, table[j]);
                tableChar = tableCharTo;

                if (j == table.length - 1) {
                    tableChar.addTransition(joinTable, ' ');
                }
            }
        }

        State onO = new State("onO");
        State onN = new State("onN");
        State on_ = new State("on_");

        State columnJoin = new State("columnJoin");
        State columnJoin_ = new State("columnJoin_");

        joinTable.addTransition(onO, 'o');
        onO.addTransition(onN, 'n');
        onN.addTransition(on_, ' ');

        for (char c : sigma) {
            on_.addTransition(columnJoin, c);
            columnJoin.addTransition(columnJoin, c);
        }

        columnJoin.addTransition(columnJoin_, ' ');
        columnJoin_.addTransition(equal, '=');

        return qInitial;
    }

    public NFA getNfa() {
        return nfa;
    }

    public void setNfa(NFA nfa) {
        this.nfa = nfa;
    }

}
