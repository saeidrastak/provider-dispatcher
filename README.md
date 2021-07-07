# Provider Dispatcher
Assumptions:
- We have 3 providers
- We have 30 numbers (equally split between the 3 providers)
- Each provider services 10 numbers
- We can have up to 5 calls at a given time
- 1 of the providers has priority over the rest

## How it works
Make a random selection from the numbers that have not call and make a call for them. If all calls are full and the next
random selected number is not of a priority provider, then reject the call if it is of a priority, then kill one of the
ongoing non-priority calls and start the priority.

As a result at a given time we should have a full stack with priority calls.