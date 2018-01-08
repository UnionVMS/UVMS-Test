# UVMS-Test

This repository now contains the framework to test the Bussiness rules.

# How to

In order to create a new test the file tests.csv needs to be updated with a new line.
When running tests the new line will be picked up and executed

TEST_ID - id to easy identify the test
PASS - TRUE/FALSE - do we expect the rule tested to pass or fail
RULE_ID - the br_id of the rule we are testing for
E/W - do we expect (E)rror or (W)arning
LEVEL - the reported level of the rule
TemplateFile - the file containing the XML template for the message to be sent
Param1 - params to fill in the XML template with (these can be as many as we want). Params will be filled in the template in the order of definition
Param2
	.
	.
	.
Param14
Param15

Of course we also need to pickup an already existing template file or generate a new one and reference it in the CSV file.



The framework picks up the CSV file and for each line fills in the XML template with the values defined in the params and sends it to the FLUXActivity plugin JMS queue.
The rules are applied and the result is loogged in the exchange.log table.

Each message is identified by a guid generated before sending and the script searches for the result and tries to identify the if the result contians the rule mentioned under RULE_ID.

Besides the tests for the specific rule some generic tests are also runned on the result XML like:
- if the reported status is correct (OK, WOK, NOK)
- all reported rule failures have at least one XPATH reported for the node that generated the error

After tests ran we generate a html report with all tests and their results.