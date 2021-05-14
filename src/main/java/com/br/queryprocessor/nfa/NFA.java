package com.br.queryprocessor.nfa;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class NFA {
    private State q;

    public NFA(State q) {
        this.q = q;
    }

    public boolean run(String w) {
        w = w.toLowerCase();
        Set<State> states = new HashSet<>();
        states.add(q);

        for (int k = 0; k < w.length(); k++) {
            char ch = w.charAt(k);

            Set<State> newStates = new HashSet<>();

            for (State s : states) {
                newStates = merge(newStates, s.states(ch));
            }

            states = newStates;
//            System.out.println(ch + " => " + states);
            if (states.size() == 0) break;
        }

        return valid(states);
    }

    private static Set<State> merge(Set<State> a, Set<State> b) {
        Set<State> r = new HashSet<>();

        for (State s : a) if (!r.contains(s)) r.add(s);
        for (State s : b) if (!r.contains(s)) r.add(s);

        return r;
    }

    public boolean valid(Set<State> qs) {
        if (qs == null || qs.size() == 0) return false;

        for (State s : qs)
            if (s.isFinal())
                return true;

        return false;
    }
}
