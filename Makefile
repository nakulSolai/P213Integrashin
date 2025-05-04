NONTESTS = Backend_Placeholder Backend BackendInterface BaseGraph Frontend FrontendInterface Graph_Placeholder GraphADT MapADT PlaceholderMap WebApp

TESTS = BackendTests FrontendTests P210SubmissionChecker P212SubmissionChecker DijkstraGraph # HashtableMap

NONTESTCLASSES = $(foreach nonTest, $(NONTESTS), $(nonTest).class)

TESTCLASSES =  $(foreach test, $(TESTS), $(test).class)

NONTESTFILES = $(foreach nonTest, $(NONTESTS), $(nonTest).java)

TESTFILES = $(foreach test, $(TESTS), $(test).java)


startServer: $(NONTESTCLASSES) $(TESTCLASSES)
	java WebApp
#	firefox https://cs400-web.cs.wisc.edu/solai/

runAllTests: $(TESTCLASSES)
	@for test in $(TESTS); do \
		java -jar ../junit5.jar -cp . -c $$test ; \
	done

$(TESTCLASSES): $(NONTESTSCLASSES) $(TESTFILES)
	@for testFile in $(TESTFILES); do \
		javac -cp .:../junit5.jar $$testFile ; \
	done
	javac -cp .:../junit5.jar HashtableMap.java

$(NONTESTCLASSES): $(NONTESTFILES)
	@for nonTestFile in $(NONTESTFILES); do \
    javac $$nonTestFile ; \
	done

clean:
	rm -f *.class


