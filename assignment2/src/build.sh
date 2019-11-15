#!/usr/bin/env bash
jjtree CCALParser.jjt;
javacc CCALParser.jj;
javac *.java