## Group members
&nbsp;

| NMec | Name | email | Contribution (%) | Detailed contribution [1]
|:-:|:--|:--|:-:|:--|
| 98119 | Fábio Martins | fabio.m@ua.pt | 20% | primary-grammar (20%)<br>primary-semantic-analysis (20%)<br>code-generation (20%)<br>secondary-grammar (20%)<br>secondary-semantic-analysis (20%)<br>secondary-interpretation/secondary-code-generation (20%)<br>examples (20%)<br>testing (20%)|
| 97484 | Pedro Monteiro | pmapm@ua.pt | 20% | primary-grammar (20%)<br>primary-semantic-analysis (20%)<br>code-generation (20%)<br>secondary-grammar (20%)<br>secondary-semantic-analysis (20%)<br>secondary-interpretation/secondary-code-generation (20%)<br>examples (20%)<br>testing (20%)|
| 98629 | Marta Fradique | martafradique@ua.pt | 15% | primary-grammar (15%)<br>primary-semantic-analysis (15%)<br>code-generation (15%)<br>secondary-grammar (15%)<br>secondary-semantic-analysis (15%)<br>secondary-interpretation/secondary-code-generation (15%)<br>examples (15%)<br>testing (15%)|
| 93107 | Eduardo Santos | eduardosantoshf@ua.pt | 15% | primary-grammar (15%)<br>primary-semantic-analysis (15%)<br>code-generation (15%)<br>secondary-grammar (15%)<br>secondary-semantic-analysis (15%)<br>secondary-interpretation/secondary-code-generation (15%)<br>examples (15%)<br>testing (15%)| 
| 98597 | José Trigo | josetrigo@ua.pt | 15% | primary-grammar (15%)<br>primary-semantic-analysis (15%)<br>code-generation (15%)<br>secondary-grammar (15%)<br>secondary-semantic-analysis (15%)<br>secondary-interpretation/secondary-code-generation (15%)<br>examples (15%)<br>testing (15%)|
| 97541 | André Gomes | alg@ua.pt | 15% | primary-grammar (15%)<br>primary-semantic-analysis (15%)<br>code-generation (15%)<br>secondary-grammar (15%)<br>secondary-semantic-analysis (15%)<br>secondary-interpretation/secondary-code-generation (15%)<br>examples (15%)<br>testing (15%)|

## Material to be evaluated

- Beware that **only** the code in the **master** branch will be considered for evaluation.

## Compilation & Run

- Compile with bash script: [`../src/run.sh`](https://github.com/detiuaveiro/bdex-comp-09/blob/master/src/run.sh)

This script compiles all the code, starting with the visitor of 2nd grammar (antlr4 -visitor SecondaryGrammar/ReadFile.g4)
Then javac SecondaryGrammar/*.java and antlr4-build.

To run the examples the script will iterate all files in the examples' folder.

## Working examples (at least two)

Examples:

All examples are compiled and ran through the `run.sh` script .

1. [`../examples/declarationExample.txt`](https://github.com/detiuaveiro/bdex-comp-09/blob/master/examples/declarationExample.txt)

    Tested the declaration of variables, assignment, if statement and for cycle.

2. [`../examples/examples.txt`](https://github.com/detiuaveiro/bdex-comp-09/blob/master/examples/examples.txt)

    Tested the addition of data.

3. [`../examples/table.txt`](https://github.com/detiuaveiro/bdex-comp-09/blob/master/examples/table.txt)

    General example of table manipulation.

4. [`../examples/biblioteca.txt`](https://github.com/detiuaveiro/bdex-comp-09/blob/master/examples/biblioteca.txt)

    Pratical example of our program, covering the majority of the features.

## Semantic error examples

1. [`../examples/SemanticCheck.txt`](https://github.com/detiuaveiro/bdex-comp-09/blob/master/examples/SemanticCheck.txt)

    Error handling and examples.

