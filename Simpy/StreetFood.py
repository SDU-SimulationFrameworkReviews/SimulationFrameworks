import random
import simpy
import time

RANDOM_SEED = 43;
RESTAURANT_CUSTOMERS = 100000;

def customerGenerator(env, totalCustomers, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers):
    '''Source Generates customers randomly'''
    for i in range(totalCustomers):
        customerName = "Customer"+str(i);
        env.process(enterStreetFood(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers))
        yield env.timeout(random.uniform(10, 50))

def enterStreetFood(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers):
    # ARTIFCACT -> TravelTo
    yield env.timeout(15)
    # ARTIFACT -> SitAtTable + Release when exiting method
    with tables.request() as req:
        yield req
        # ARTIFACT -> TravelToFoodOrDrink
        yield env.timeout(15)
        foodOrDrinkFlow = foodOrDrink(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers)
        yield env.process(foodOrDrinkFlow)

    # At this point a customer released the table at the end of the flow
    # ARTIFACT -> TravelToCheckout
    yield env.timeout(15)
    with cashiers.request() as req:
        yield req
        checkoutTime = random.uniform(15, 45)
        yield env.timeout(checkoutTime)
    # The flow is done now and the customer leave

def foodOrDrink(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers):
    # ARTIFACT -> FoodOrDrink + FoodOrDrinkDist
    yield env.timeout(0) # <-- Generators has to produce a yield statement to circumvent we yield 0
    branch = random.randint(1, 10);
    if branch > 4:  # 60% chance
        foodStandChoiceFlow = foodStandChoice(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers,burgerWorkers, cashiers)
        yield env.process(foodStandChoiceFlow)
    else:  # 40% chance
        orderDrinkFlow = orderDrink(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers)
        yield env.process(orderDrinkFlow)

def foodStandChoice(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers):
    # ARTIFCAT -> TravelToDecideFood + AlsoOrderFood
    yield env.timeout(15)
    # ARTIFACT -> FoodStandChoice + FoodStandDist
    branch = random.randint(1, 100)
    # ARTIFACT -> TravelToPizza + TravelToChinese + TravelToBurger
    yield env.timeout(15)
    if branch < 34:
        # ARTIFACT -> Order Pizza + WaitPizza
        with pizzaWorkers.request() as req:
            yield req
            pizzaWaitTime = random.uniform(15, 45)
            yield env.timeout(pizzaWaitTime)
        # ARTIFACT -> EatPizza
        pizzaEatTime = random.uniform(15, 45)
        yield env.timeout(pizzaEatTime)

    elif branch < 67:
        # ARTIFACT -> OrderChinese + WaitChinese
        with chineseWorkers.request() as req:
            yield req
            chineseWaitTime = random.uniform(15, 45)
            yield env.timeout(chineseWaitTime)
        # ARTIFACT -> EatChinese
        chineseEatTime = random.uniform(15, 45)
        yield env.timeout(chineseEatTime)
    else:
        # ARTIFACT -> OrderBurger + WaitBurger
        with burgerWorkers.request() as req:
            yield req
            burgerWaitTime = random.uniform(15, 45)
            yield env.timeout(burgerWaitTime)
        # ARTIFACT -> EatBurger
        burgerEatTime = random.uniform(15, 45)
        yield env.timeout(burgerEatTime)

    # Once the respective branch is finished go to the OrderAgainOrLeaveFlow
    orderAgainOrLeaveFlow = orderAgainOrLeave(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers)
    yield env.process(orderAgainOrLeaveFlow)

def orderDrink(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers):
    # ARTIFICAT -> TravelToDrink
    yield env.timeout(15)
    # ARTIFACT -> OrderDrink + Pouring + ReceiveDrink
    with drinkWorkers.request() as req:
        yield req
        servingTime = random.uniform(15, 45)
        yield env.timeout(servingTime)

    # ARTIFACT -> Decide
    yield env.timeout(5)

    # ARTIFACT -> OnlyDrinkOr?
    branch = random.randint(1,10)
    if branch < 5:
        foodStandChoiceFlow = foodStandChoice(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers)
        yield env.process(foodStandChoiceFlow)
    else:
        # ARTIFACT -> Drinking + DrinkingStep2
        drinkingTime = random.uniform(20, 60) + 5
        yield env.timeout(drinkingTime)
        orderAgainOrLeaveFlow = orderAgainOrLeave(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers)
        yield env.process(orderAgainOrLeaveFlow)

def orderAgainOrLeave(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers):
    # ARTIFACT -> OrderAgainOrLeave + OrderAgainOrLeaveDist
    branch = random.randint(1, 10)
    if branch > 3: # 70% chance
        # ARTIFACT -> LeaveTable
        yield env.timeout(15)
        # ARTIFACT --> ReleaseTable --> We are done with the table so return at this point continue the remaining execution in the customerGenerator top level method
    else: # 30% chance
        # ARTIFACT -> GoToFoodOrDrinkStep 1,2 and 3.
        yield env.timeout(15)
        foodOrDrinkFlow = foodOrDrink(env, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers)
        yield env.process(foodOrDrinkFlow)



# Setup and start the simulation
print("Starting Simulation")
def simulate():
    for i in range(19):

        random.seed(RANDOM_SEED * i);
        env = simpy.Environment();
        startTime = time.time()

        for j in range(10):
            # Start processes and run
            tables = simpy.Resource(env, capacity=100)
            drinkWorkers = simpy.Resource(env, capacity=3)
            chineseWorkers = simpy.Resource(env, capacity=3)
            pizzaWorkers = simpy.Resource(env, capacity=3)
            burgerWorkers = simpy.Resource(env, capacity=3)
            cashiers = simpy.Resource(env, capacity=3)

            env.process(customerGenerator(env, RESTAURANT_CUSTOMERS, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers))
            env.run();
        endTime = time.time()
        elapsedTime = endTime - startTime
        print(str(elapsedTime / 10) + ";" + str(RESTAURANT_CUSTOMERS + (i * 50000)))

def test():

    testCustomer = 100000;
    random.seed(RANDOM_SEED);
    env = simpy.Environment();

    # Start processes and run
    startTime = time.time()
    tables = simpy.Resource(env, capacity=100)
    drinkWorkers = simpy.Resource(env, capacity=3)
    chineseWorkers = simpy.Resource(env, capacity=3)
    pizzaWorkers = simpy.Resource(env, capacity=3)
    burgerWorkers = simpy.Resource(env, capacity=3)
    cashiers = simpy.Resource(env, capacity=3)

    env.process(customerGenerator(env, testCustomer, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers))
    env.run();
    endTime = time.time()
    elapsedTime = endTime - startTime
    print(str(elapsedTime) + ";" + str(testCustomer))

test()