package com.duty.scheduler.schedulers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.duty.scheduler.models.ApplicationDay;
import com.duty.scheduler.models.Holyday;
import com.duty.scheduler.models.Schedule;
import com.duty.scheduler.models.User;
import com.duty.scheduler.models.UserActive;
import com.duty.scheduler.models.UserApplication;
import com.duty.scheduler.models.UserDuty;
import com.duty.scheduler.repository.ScheduleRepository;
import com.duty.scheduler.repository.UserDutyRepository;
import com.duty.scheduler.services.DBStatus;

public class GeneticAlgorithmScheduler implements IScheduler {

	private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.025;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;
    private static final int populationSize = 50;
    private static final int generationIterations = 300000;
    
    private static long[] userIndex;
    private static int numOfDutyUsers;
    private static int hoursInMonth;
    private static double averageDutyTime;
    
	private static ScheduleRepository scheduleRepository;
	private static UserDutyRepository userDutyRepository;
	private static LocalDate month;
	private static User generatedBy;
	private static List<UserActive> activeUsers;
	private static List<UserApplication> userApplications; 
	private static List<Holyday> holydays;
	private static DBStatus dbStatus;
		
	public GeneticAlgorithmScheduler(LocalDate month, User generatedBy, List<UserActive> activeUsers,
			List<UserApplication> userApplications, List<Holyday> holydays, ScheduleRepository scheduleRepository, UserDutyRepository userDutyRepository, DBStatus dbStatus) {
		GeneticAlgorithmScheduler.month = month;
		GeneticAlgorithmScheduler.generatedBy = generatedBy;
		GeneticAlgorithmScheduler.activeUsers = activeUsers;
		GeneticAlgorithmScheduler.userApplications = userApplications;
		GeneticAlgorithmScheduler.holydays = holydays;
		GeneticAlgorithmScheduler.scheduleRepository = scheduleRepository;
		GeneticAlgorithmScheduler.userDutyRepository = userDutyRepository;
		GeneticAlgorithmScheduler.dbStatus = dbStatus;
	}
	
	public void run() {
		
		try
		{
			dbStatus.setBusy(true);
			
			Schedule newSchedule = generateSchedule(month, generatedBy, activeUsers, userApplications, holydays);
			
			scheduleRepository.save(newSchedule);
			for(UserDuty duty : newSchedule.getUserDuties())
			{
				userDutyRepository.save(duty);
			}
			
			scheduleRepository.flush();
			userDutyRepository.flush();		
		}
		catch (Exception e)
		{
			
		}
		
		dbStatus.setBusy(false);
    }

	public Schedule generateSchedule(LocalDate month, User generatedBy, List<UserActive> activeUsers, List<UserApplication> userApplications, List<Holyday> holydays )
	{
		try 
		{
				
			numOfDutyUsers = activeUsers.size();
			userIndex = new long[numOfDutyUsers];
			for (int i = 0; i < numOfDutyUsers; i++) {
				userIndex[i] = activeUsers.get(i).getUser().getId();
	        }
			
			hoursInMonth = 0;
			//LocalDate day = LocalDate.of(month.getYear(), month.getMonth(), month.getDayOfMonth());
			for (int i = 0; i < month.lengthOfMonth(); i++) {
				LocalDate dOM = LocalDate.of(month.getYear(), month.getMonth(), i + 1);
				if(dOM.getDayOfWeek() == DayOfWeek.SATURDAY || dOM.getDayOfWeek() == DayOfWeek.SUNDAY || holydays.stream().anyMatch(h -> h.getDay().equals(dOM)))
				{
					hoursInMonth += 16;
				}
				else
				{
					hoursInMonth += 8;
				}
	        }
			averageDutyTime = hoursInMonth / numOfDutyUsers;
			
			Schedule result = new Schedule(generatedBy, month, LocalDateTime.now(), true);
			
			Set<UserDuty> userDuties = new HashSet<UserDuty>();
			
			
			//////////////////////
					  
			Population myPop = new Population(populationSize, true);  
			
			int generationCount = 0;
	
			while (generationCount < generationIterations) {
			
				//System.out.println("Generation: " + generationCount + " Best score so far: " + myPop.getFittest().getFitness());
				myPop = evolvePopulation(myPop);
				generationCount++;
			
			}
				
			Individual best = myPop.getFittest();
					
			// encoding
			int daytmp = 1;
	        int cnt = 0;
	        int value = 0;
	        for (int i = 0; i < best.getDefaultGeneLength(); i++) {
	            
	        	value += best.getSingleGene(i) * (int)Math.pow(2, 4-cnt);
	        	
	        	cnt++;
	        	
	        	if (cnt == 5) {
	        		
	        		int userInd = value % numOfDutyUsers;
	        		
	        		long userId = userIndex[userInd];
	        		LocalDate dutyDay = LocalDate.of(month.getYear(), month.getMonth(), daytmp);
	        		int dutyHours = 8;
	        		if(dutyDay.getDayOfWeek() == DayOfWeek.SATURDAY || dutyDay.getDayOfWeek() == DayOfWeek.SUNDAY || holydays.stream().anyMatch(h -> h.getDay().equals(dutyDay)))
	    			{
	        			dutyHours = 16;
	    			}
	        		
	        		for (int u = 0; u < numOfDutyUsers; u++) {
	        			User tmpUserDuty = activeUsers.get(u).getUser();
	        			
	        			if(tmpUserDuty.getId() == userId)
	        			{
	        				userDuties.add(new UserDuty(tmpUserDuty, result, dutyDay, dutyHours));
	        				break;
	        			}
	                }
	        		
	        		cnt = 0;
	        		daytmp++;
	        		value = 0;
	            }
	        	
	        	if(daytmp > month.lengthOfMonth()) break;
	        }
	        
			//////////////////////
				
			result.setUserDuties(userDuties);
			
			return result;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
					
			Schedule result = new Schedule(generatedBy, month, LocalDateTime.now(), false);
			
			Set<UserDuty> userDuties = new HashSet<UserDuty>();
			
			result.setUserDuties(userDuties);
			
			return result;
		}
				
	}
	

	public Population evolvePopulation(Population pop) {
        int elitismOffset;
        Population newPopulation = new Population(pop.getIndividuals().size(), false);

        if (elitism) {
            newPopulation.getIndividuals().add(0, pop.getFittest());
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }

        for (int i = elitismOffset; i < pop.getIndividuals().size(); i++) {
            Individual indiv1 = tournamentSelection(pop);
            Individual indiv2 = tournamentSelection(pop);
            Individual newIndiv = crossover(indiv1, indiv2);
            newPopulation.getIndividuals().add(i, newIndiv);
        }

        for (int i = elitismOffset; i < newPopulation.getIndividuals().size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }
	
	private Individual crossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual();
        boolean doCross = false;
        
        for (int i = 0; i < newSol.getDefaultGeneLength(); i++) {
        	
        	if(i % 5 == 0) doCross = (Math.random() <= uniformRate);
        	
            if (doCross) {
                newSol.setSingleGene(i, indiv1.getSingleGene(i));
            } else {
                newSol.setSingleGene(i, indiv2.getSingleGene(i));
            }
        }
        return newSol;
    }

    private void mutate(Individual indiv) {
        for (int i = 0; i < indiv.getDefaultGeneLength(); i++) {
            if (Math.random() <= mutationRate) {
                byte gene = (byte) Math.round(Math.random());
                indiv.setSingleGene(i, gene);
            }
        }
    }

    private Individual tournamentSelection(Population pop) {
        Population tournament = new Population(tournamentSize, false);
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.getIndividuals().size());
            tournament.getIndividuals().add(i, pop.getIndividual(randomId));
        }
        Individual fittest = tournament.getFittest();
        return fittest;
    }
	
    protected static int getFitness(Individual individual) {

        int penalty = 0;
        
        Map<Long, List<Integer>> userDayMapping = new HashMap<Long, List<Integer>>();
        
        int day = 1;
        int cnt = 0;
        int value = 0;
        for (int i = 0; i < individual.getDefaultGeneLength(); i++) {
            
        	value += individual.getSingleGene(i) * (int)Math.pow(2, 4-cnt);
        	
        	cnt++;
        	
        	if (cnt == 5) {
        		
        		int userInd = value % numOfDutyUsers;
        		
        		if(userDayMapping.containsKey(userIndex[userInd]))
        		{
        			userDayMapping.get(userIndex[userInd]).add(day);
        		}
        		else
        		{
        			userDayMapping.put(userIndex[userInd], new LinkedList<Integer>(Arrays.asList(day)));      			
        		}
        		
        		cnt = 0;
        		day++;
        		value = 0;
            }
        	
        	if(day > month.lengthOfMonth()) break;
        }
        
        // EQUAL DUTY HOURS
        for (int i = 0; i < numOfDutyUsers; i++) {
        	
        	int usersHours = 0;
        	
        	if(userDayMapping.containsKey(userIndex[i]) == false)
        	{
        		penalty += 1000 * (int)Math.abs(averageDutyTime);
        		continue;
        	}
        	
        	for(int d : userDayMapping.get(userIndex[i]))
        	{
        		LocalDate dOM = LocalDate.of(month.getYear(), month.getMonth(), d);
        		if(dOM.getDayOfWeek() == DayOfWeek.SATURDAY || dOM.getDayOfWeek() == DayOfWeek.SUNDAY || holydays.stream().anyMatch(h -> h.getDay().equals(dOM)))
    			{
        			usersHours += 16;
    			}
    			else
    			{
    				usersHours += 8;
    			}
        	}
        	        	
        	penalty += 1000 * (int)Math.abs(averageDutyTime - usersHours);
        }
        
        // PREFERED DUTY DAYS
        for (int i = 0; i < numOfDutyUsers; i++) {
        	
        	long tmpUser = userIndex[i];
        	
        	if(userApplications.stream().anyMatch(uap -> uap.getUser().getId().equals(tmpUser)))
        	{
        		Set<ApplicationDay> userAppDays = userApplications.stream().filter(uap -> uap.getUser().getId().equals(tmpUser)).findFirst().get().getApplicationDays();
        		
        		if(userDayMapping.containsKey(userIndex[i]) == false)
            	{
            		continue;
            	}
        		
        		for(int d : userDayMapping.get(userIndex[i]))
            	{
            		LocalDate dOM = LocalDate.of(month.getYear(), month.getMonth(), d);
            		if(userAppDays.stream().anyMatch(uad -> uad.getDay().equals(dOM)) == false)
        			{
            			penalty += 1500;
        			}
            	}
        	}
        }
        
        // DUTY DAYS IN CONTINOUS
        int groups = 0;
		int lastNum = -1;
        for (int i = 0; i < numOfDutyUsers; i++) {
        	
        	long tmpUser = userIndex[i];
        	boolean userAppDaysInContinous = true;
        	
        	if(userApplications.stream().anyMatch(uap -> uap.getUser().getId().equals(tmpUser)))
        	{
        		userAppDaysInContinous = userApplications.stream().filter(uap -> uap.getUser().getId().equals(tmpUser)).findFirst().get().getGrouped();
        	}  
        	
        	if(userAppDaysInContinous)
        	{
        		groups = 0;
        		lastNum = -1;
        		
        		if(userDayMapping.containsKey(userIndex[i]) == false)
            	{
            		continue;
            	}
        		
        		Collections.sort(userDayMapping.get(userIndex[i]));
        		for(int d : userDayMapping.get(userIndex[i]))
            	{
            		if(d > lastNum + 1)
            		{
            			groups++;
            		}
            		lastNum = d;
            	}
        		
        		penalty += 1500 * (groups - 1);
        	}
        	
        }
        
        return 100000 - penalty;
    }
}