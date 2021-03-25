using SimJulia
using Distributions
using ResumableFunctions

@resumable function CustomerGenerator(sim::Simulation, totalCustomers::Number, tables:: Resource, drinkWorkers:: Resource, chineseWorkers:: Resource, pizzaWorkers:: Resource, burgerWorkers:: Resource, cashiers::Resource)
    # Generates customers uniformly
    for i in 1:totalCustomers
        customerName = "Customer$(i) arrived at time:$(sim.time)"
        @process EnterStreetFood(sim, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers)
        
        @yield timeout(sim, rand(Uniform(10, 50)));
    end
end

@resumable function EnterStreetFood(sim::Simulation, customerName::String, tables:: Resource, drinkWorkers:: Resource, chineseWorkers:: Resource, pizzaWorkers:: Resource, burgerWorkers:: Resource, cashiers::Resource)
    
    #println("Customer arrived at: $(sim.time)")
    # ARTIFACT -> TravelTo
    @yield timeout(sim, 0.15)
    
    # ARTIFACT -> SitAtTable
    @yield request(tables)
    #println("Get table")
    # ARTIFACT -> TravelToFoodOrDrink
    @yield timeout(sim, 0.15)
    @yield @process FoodOrDrink(sim, customerName, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers)
    #println("Release table")
    # Done with the table release resource
    @yield release(tables);

    # ARTIFACT -> TravelToCheckout
    @yield timeout(sim, 0.15);
    # Wait for a cashier
    @yield request(cashiers)
    #println("Get cashier")
    # Checkout time
    @yield timeout(sim, rand(Uniform(15, 45)))
    # Done!
end

@resumable function FoodOrDrink(sim::Simulation, customerName::String, tables:: Resource, drinkWorkers:: Resource, chineseWorkers:: Resource, pizzaWorkers:: Resource, burgerWorkers:: Resource, cashiers::Resource)
    @yield timeout(sim, 0)
    branch = rand() * 10;
    if branch > 4
        @yield @process FoodStandChoice(sim::Simulation, customerName::String, tables:: Resource, drinkWorkers:: Resource, chineseWorkers:: Resource, pizzaWorkers:: Resource, burgerWorkers:: Resource, cashiers::Resource)
    else
        @yield @process OrderDrink(sim::Simulation, customerName::String, tables:: Resource, drinkWorkers:: Resource, chineseWorkers:: Resource, pizzaWorkers:: Resource, burgerWorkers:: Resource, cashiers::Resource)
    end
end

@resumable function FoodStandChoice(sim::Simulation, customerName::String, tables:: Resource, drinkWorkers:: Resource, chineseWorkers:: Resource, pizzaWorkers:: Resource, burgerWorkers:: Resource, cashiers::Resource)
    #println("Go to food standChoice")
    @yield timeout(sim, 0.15)
    branch = rand() * 100
    @yield timeout(sim, 0.15)

    if branch < 34
        #println("Order wait and eat pizza")
        @yield request(pizzaWorkers)
        @yield timeout(sim, rand(Uniform(15,45)))
        @yield release(pizzaWorkers)
        @yield timeout(sim, rand(Uniform(15,45)))
    elseif branch < 67
        #println("Order wait and eat chinese")
        @yield request(chineseWorkers)
        @yield timeout(sim, rand(Uniform(15,45)))
        @yield release(chineseWorkers)
        @yield timeout(sim, rand(Uniform(15,45)))
    else
        #println("Order wait and eat burger")
        @yield request(burgerWorkers)
        @yield timeout(sim, rand(Uniform(15,45)))
        @yield release(burgerWorkers)
        @yield timeout(sim, rand(Uniform(15,45)))
    end
    @yield @process OrderAgainOrLeave(sim::Simulation, customerName::String, tables:: Resource, drinkWorkers:: Resource, chineseWorkers:: Resource, pizzaWorkers:: Resource, burgerWorkers:: Resource, cashiers::Resource)
end

@resumable function OrderDrink(sim::Simulation, customerName::String, tables:: Resource, drinkWorkers:: Resource, chineseWorkers:: Resource, pizzaWorkers:: Resource, burgerWorkers:: Resource, cashiers::Resource)
    # Artifact -> TravelToDrink
    #println("Going to drink")
    @yield timeout(sim, 0.15)
    @yield request(drinkWorkers)
    #println("Drinkworker is pouring you drink")
    @yield timeout(sim, rand(Uniform(15, 45)))
    @yield release(drinkWorkers)
    #println("You receive drink")
    @yield timeout(sim, 5)
    branch = rand() * 10
    if branch < 5
        @yield @process FoodStandChoice(sim::Simulation, customerName::String, tables:: Resource, drinkWorkers:: Resource, chineseWorkers:: Resource, pizzaWorkers:: Resource, burgerWorkers:: Resource, cashiers::Resource)
    else
        #println("Only drink")
        @yield timeout(sim, rand(Uniform(20, 60)) + 5)
        @yield @process OrderAgainOrLeave(sim::Simulation, customerName::String, tables:: Resource, drinkWorkers:: Resource, chineseWorkers:: Resource, pizzaWorkers:: Resource, burgerWorkers:: Resource, cashiers::Resource)
    end
end

@resumable function OrderAgainOrLeave(sim::Simulation, customerName::String, tables:: Resource, drinkWorkers:: Resource, chineseWorkers:: Resource, pizzaWorkers:: Resource, burgerWorkers:: Resource, cashiers::Resource)
    branch = rand() * 10;
    if branch > 2
        @yield timeout(sim, 0.15)
    else
        #println("Order again!")
        @yield timeout(sim, 0.15)
        #@yield @process FoodOrDrink(sim::Simulation, customerName::String, tables:: Resource, drinkWorkers:: Resource, chineseWorkers:: Resource, pizzaWorkers:: Resource, burgerWorkers:: Resource, cashiers::Resource)
    end
end

function test(numberOfCustomers:: Number)
    #println(numberOfCustomers)
    sim = Simulation();
    # Resources
    tables = Resource(sim, 100)
    drinkWorkers = Resource(sim, 3)
    chineseWorkers = Resource(sim, 3)
    pizzaWorkers = Resource(sim, 3)
    burgerWorkers = Resource(sim, 3)
    cashiers = Resource(sim, 3)
    #startTime = time()
    @process CustomerGenerator(sim, numberOfCustomers, tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers)
    run(sim)
    #endTime = time()
    #elapsedTime = (endTime - startTime);
    #println(elapsedTime)
end

#function simulate()
#    for i in 0:19
#        startTime = time()
#        for j in 0:9
#
#            # Environment
#            sim = Simulation();
#
#            # Resources
#            tables = Resource(sim, 100)
#            drinkWorkers = Resource(sim, 3)
#            chineseWorkers = Resource(sim, 3)
#            pizzaWorkers = Resource(sim, 3)
#            burgerWorkers = Resource(sim, 3)
#            cashiers = Resource(sim, 3)
#            
#            # Timing
#            jStartTime = time()
#            @process CustomerGenerator(sim, 100000 + (i * 50000), tables, drinkWorkers, chineseWorkers, pizzaWorkers, burgerWorkers, cashiers)
#            run(sim)
#            jEndTime = time()
#        end
#        endTime = time()
#        avgElapsedTime = (endTime - startTime) / 10
#        println("$(avgElapsedTime);$(100000 + (i * 50000))")
#    end
#end

#simulate();

function main(args)
    test(parse(Int64, args[1]))
end

main(ARGS)
