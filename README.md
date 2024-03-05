# Project 4: Autocomplete

This is the directions document for Project 4 Autocomplete in CompSci 201 at Duke University, Spring 2024.

**See [the details document](docs/details.md) for information** on using Git, starting the project, and more details about the project including information about the classes and concepts that are outlined briefly below. You'll absolutely need to read the information in the [details document](docs/details.md) to understand how the classes in this project work independently and together. The _details_ document also contains project-specific details, this document provides a high-level overview of the assignment.

## Overview: What to Do

For details, see the [details document](docs/details.md) -- you'll find a high-level overview here. To complete the assignment you'll do the following, roughly in the order shown.

1. Run `AutocompleteMain` using `BruteAutoComplete` (complete in the starter code) to see how the autocomplete application works. 
2. Implement the `compare` method in the `PrefixComparator` class that is used in the `BinarySearchAutocomplete` class. Test with `TestTerm`.
3. Implement two methods in `BinarySearchLibrary`: `firstIndex` and `lastIndex`, both of which will use the `PrefixComparator` you completed in the previous step. Test with `TestBinarySearchLibrary`. **We recommend you try to finish these first three steps early**.
4. Finish implementing `BinarySearchAutocomplete` that extends `Autocompletor` by completing the `topMatches` method. This will use the `firstIndex` and `lastIndex` methods you wrote in the previous step and the code in `BruteAutocomplete.topMatches`  as a model. Test with `TestBinarySearchAutocomplete` and running `AutocompleteMain` using `BinarySearchAutocomplete`.
5. Create and implement a new class `HashListAutocomplete` that implements interface `Autocompletor`. Test by running `AutocompleteMain` using `HashListAutocomplete`.
6. Run benchmarks and answer analysis questions. Submit code, analysis.


## Part 1: Run Autocomplete Main

When you fork and clone the project you'll be able to run the `main` method of `AutocompleteMain`. Doing so will launch a "GUI" (Graphical User Interface) that allows you to select a data file. The data file will determine the set of possible words to be recommended by the autocompleter application, and also includes weights for how common the words are. Several such files are included along with this project.

Once you select a file, the GUI will prompt you to enter a term. As you type, you should see the most common words that complete what you have typed so far appearing. For example, if you run `AutocompleteMain` and select the file `words-333333.txt` from the data folder you should see the output below shown in the GUI window for the search query **auto**.  You'll use this same search term, `auto` to test the other implementations you develop. **Refer back to this visual to see if your new classes and code work.**

<div align="center">
  <img src="p4-figures/astrachanSearch.png">
</div>

By default, `AutocompleteMain` is using `BruteAutocomplete` to find the correct words/weights (terms) to display. You will write two additional implementations of the `Autocompleter` interface: `BinarySearchAutocomplete` and `HashListAutocomplete`. When you finish one, you can again run `AutocompleteMain` using your new implementation by changing the `AUTOCOMPLETOR_CLASS_NAME` just before the `main` method of `AutocompleteMain`.


## Part 2: Implement the `compare` method in `PrefixComparator`

A `PrefixComparator` object is obtained by calling `PrefixComparator.getComparator` with an integer argument `r`, the size of the prefix for comparison purposes. The value is stored in the instance variable `myPrefixSize` as you'll see in the code. This class is used in `BinarySearchAutocomplete`, but not in `BruteAutocomplete`. **See the [details document](docs/details.md) for details on the class and method**.

You can test your code with the `TestTerm` JUnit class which has several tests for the `PrefixComparator` class.

## Part 3: Implement `BinarySearchLibrary`

The class `BinarySearchLibrary` stores static utility methods used in the implementation of the `BinarySearchAutocomplete` class. You will need to implement two methods in particular: `firstIndex` and `lastIndex`. Both are variants on the Java API [`Collections.binarySearch(list, key, c)`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Collections.html#binarySearch(java.util.List,T,java.util.Comparator)) method that, in addition to returning an index `dex` such that `c.compare(list.get(dex), key)==0`, also guarantee to find the first or last such index respectively. **Details on the methods you write are [in the details document](docs/details.md).**

`BinarySearchAutocomplete` will use these methods along with the `PrefixComparator` you already completed to efficiently determine the *range of possible completions of a given prefix of a word typed so far*.

## Part 4: Finish Implementing `topMatches` in `BinarySearchAutocomplete`

Once you've implemented the methods in class `BinarySearchLibrary`, you'll still need to implement code for `topMatches` in the `BinarySearchAutocomplete` class -- a method required as specified in the `Autocompletor` interface. The other methods in `BinarySearchAutocomplete` are written, though two rely on the code you implemented in `BinarySearchLibrary`.

There is a comment in the `topMatches` method indicating where you need to add more to complete the implementation. You can expand below for more details on the code already written in `topMatches` that you do not need to change.

Code in static methods `firstIndexOf` and `lastIndexOf` is written to use the API exported by `BinarySearchLibrary`. You'll see that the `Term[]` parameter to these methods is transformed to a `List<Term>` since that's the type of parameter that must be passed to `BinarySearchLibrary` methods. 

You'll also see a `Term` object called `dummy` created from the `String` passed to `topMatches`. The weight for the `Term` doesn't matter since only the `String` field of the `Term` is used in `firstIndex` and `lastIndex` calls.

**See the [details document](docs/details.md) for more information on this class and method.**

## Part 5: Implement `HashListAutocomplete`

In this part, you will provide one more implementation of the `Autocompletor` interface, this time from scratch. Unlike `BruteAutocomplete` and `BinarySearchAutocomplete`, this third implementation will be based on the use of a `HashMap` instead of the binary search algorithm. This class will provide an `O(1)` implementation of `topMatches` --- with a tradeoff of requiring more memory.

A skeleton of `HashListAutocomplete` can be found in the `HashListAutocomplete.java` file that `implements` the `Autocompletor` interface. **For details about the class and code you write see [the details document](docs/details.md).**


## Analysis Questions and Benchmarking

You'll submit the analysis as a PDF separate from the code in Gradescope. 

**Question 1.** Inside of `BenchmarkForAutocomplete`, uncomment the two other implementation names so that `myCompletorNames` has all three Strings: `"BruteAutocomplete"`, `"BinarySearchAutocomplete"`, and `"HashListAutocomplete"` (if you want to benchmark only a subset of these, perhaps because one isn't working, just leave it commented out).

Run `BenchmarkForAutocomplete` three times, once for each of the files in the Benchmark program: `threeletterwords.txt`, `fourletterwords.txt`, and `alexa.txt`. You can change which file is being used inside of the `doMark` method. **Copy and paste all three results into your analysis**. An example and detailed information about the output is described in the expandable section below.

#### Benchmarking Details

On Professor Steiger's laptop, the first few lines are what's shown below for `data/threeletterwords.txt` (in addition, the `sizeInBytes` for the implementations are shown at the bottom). These numbers are for a file of every three letter word "aaa, "aab", … "zzy", "zzz", not actual words, but 3-character strings. All times are listed in seconds.

- The `init time` data shows how long it took to initialize the different implementations.
- The `search` column shows the prefix being used to search for autocompletions; unlabeled "search" is for an empty string `""` which matches on every term. 
- The `size` column shows how many terms have `search` as a prefix. This is described as `M` earlier in [part 4](#part-4-finish-implementing-topmatches-in-binarysearchautocomplete).
- `#match` shows the number of highest weight results being returned by `topMatches`. This is described as `k` earlier in [part 4](#part-4-finish-implementing-topmatches-in-binarysearchautocomplete).
- The next three columns give the running time in seconds for `topMatches` with the given parameters for the different implementations.


```
init time: 0.004612     for BruteAutocomplete
init time: 0.003348     for BinarySearchAutocomplete
init time: 0.03887      for HashListAutocomplete
search  size    #match  BruteAutoc      BinarySear      HashListAu
        17576   50      0.00191738      0.00306458      0.00001950
        17576   50      0.00039575      0.00198267      0.00000546
a       676     50      0.00034438      0.00014479      0.00000942
a       676     50      0.00035567      0.00015113      0.00000350
b       676     50      0.00016033      0.00011954      0.00000292
```

**Question 2.** Let `N` be the total number of terms, let `M` be the number of terms that prefix-match a given `search` term (the `size` column above), and let `k` be the number of highest weight terms returned by `topMatches` (the `#match` column above). The runtime complexity of `BruteAutocomplete` is `O(N + M log(k))`. The runtime complexity of `BinarySearchAutocomplete` is `O(log(N) + M log(k))`. Yet you should notice (as seen in the example timing above) that `BruteAutocomplete` is similarly efficient or even slightly more efficient than `BinarySearchAutocomplete` on the empty `search` String `""`. Answer the following:
- Explain why this observation (that `BruteAutocomplete` is similarly efficient or even slightly more efficient than `BinarySearchAutocomplete` on the empty `search` String `""`) makes sense given the values of `N` and `M` -- in other words, is this an anomaly based on small clocktimes or is there a reason for it given the values of N, M, and what will happen with BinarySearch when searching for the empty string?

**Question 3.** Run the `BenchmarkForAutocomplete` again using `alexa.txt` but doubling `matchSize` to `100` (`matchSize` is specified in the `runAM` method). Again copy and paste your results. Recall that `matchSize` determines `k`, the number of highest weight terms returned by `topMatches` (the `#match` column above). Do your data support the hypothesis that the dependence of the runtime on `k` is logarithmic for `BruteAutocomplete` and `BinarySearchAutocomplete`?

**Question 4.** Briefly explain why `HashListAutocomplete` is much more efficient in terms of the empirical runtime of `topMatches`, but uses more memory than the other `Autocomplete` implementations.


## Submitting and Grading 

Push your code to Git. Do this often. Once you have run and tested your completed program locally:

1. Submit your code on gradescope to the autograder.
2. Submit a PDF to Gradescope in the separate Analysis assignment. Be sure to mark pages for the questions as explained in the [Gradescope documentation here](https://help.gradescope.com/article/ccbpppziu9-student-submit-work#submitting_a_pdf).

### Grading

| Points | Grading Criteria |
| ------ | ------ |
| 4 | Code for Comparator|
| 8 |  Code for BinarySearchLibrary firstIndex and lastIndex |
| 6 | Code for BinarySearchAutocomplete.topMatches |
| 9 | Code for HashListAutocomplete |
| 1 | API |
| 8 | Analysis |

As for all projects, we will scale the coding and analysis to be 80% and 20% of the overall project grade, respectively.
