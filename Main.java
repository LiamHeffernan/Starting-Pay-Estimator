// For JP Morgan Chase's challenge on addressing a social issue
// This program estimates starting yearly salaries for new hires at companies
// This program is meant to address the issue of pay discrepancies between workers...
// ...most notably addressing the gender pay gap

import java.io.*;
import static java.lang.System.*;
import java.text.DecimalFormat;
import java.util.*;

public class Main
{   
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        Scanner input = new Scanner(in);
        
        // Preferred education of employer
        Scanner education_file = new Scanner(new File("Preferred_Education.txt"));
        ArrayList<String> preferred_education = new ArrayList<>();
        while (education_file.hasNext())
            preferred_education.add(education_file.nextLine());
        
        // Rank potential companies that new hire may have worked at
        // Companies in lower-indexed ArrayLists means they are of a higher tier
        ArrayList<ArrayList<String>> companies = company_tier_list();
        
        // Who is the new hire? Pull up their information.
        out.println("Enter information file of new hire: ");
        Scanner new_hire_file = new Scanner(new File(input.next()));
        Hire new_hire = new Hire();
        new_hire.first_name = new_hire_file.next();
        new_hire.last_name = new_hire_file.next();
        new_hire_file.nextLine();
        new_hire.education_level = new_hire_file.nextLine();
        while (new_hire_file.hasNextLine())
            new_hire.work_experience.add(new Job(new_hire_file.nextLine(), new_hire_file.nextLine(),
                                                 new_hire_file.nextLine(), new_hire_file.nextLine()));
        
        // What is the new hire's new job? Pull up information on this job
        out.println("Enter job file of new hire: ");
        Scanner new_job_file = new Scanner(new File(input.next()));
        Job new_job = new Job(new_job_file.nextLine(), new_job_file.nextLine(), new_job_file.nextLine(), "-1");
        
        double estimate = estimate(new_hire, new_job, preferred_education, companies);
        out.println("Suggested starting pay is $" + new DecimalFormat("#.##").format(estimate));
    }
    
    public static ArrayList<ArrayList<String>> company_tier_list() throws FileNotFoundException
    {
        Scanner companies_file = new Scanner(new File("Companies.txt"));
        ArrayList<ArrayList<String>> companies = new ArrayList<>();
        
        String company_name;
        ArrayList<String> tier = new ArrayList<>();
        companies_file.nextLine();
        
        while (companies_file.hasNextLine())
        {
            company_name = companies_file.nextLine();
            
            if (company_name.contains("Tier"))
            {
                companies.add(tier);
                tier = new ArrayList<>();
            }
            
            else tier.add(company_name);
        }
        
        for (ArrayList<String> tiers : companies)
            for (String company : tiers)
                out.println(company);
        
        return companies;
    }
    
    public static double estimate(Hire new_hire, Job new_job, ArrayList<String> preferred_education,
                                  ArrayList<ArrayList<String>> companies)
    {
        double pay_differential = new_job.pay / 10.0;
        double pay_multiplier = 1.0;
        
        if (new_hire.education_level.contains("A.S."))
            pay_multiplier += 0.25;
        if (new_hire.education_level.contains("B.S."))
            pay_multiplier += 0.5;
        if (new_hire.education_level.contains("M.S."))
            pay_multiplier += 0.75;
        if (new_hire.education_level.contains("PhD"))
            pay_multiplier += 1.0;
        
        boolean has_preferred_education = false;
        for (int x = 0; x < preferred_education.size(); x++)
            if (new_hire.education_level.contains(preferred_education.get(x)))
                has_preferred_education = true;
        if (!has_preferred_education)
            pay_multiplier *= 0.5;
        
        for (int x = 0; x < companies.size(); x++)
        {
            double tier_multiplier = 1.0;
            
            for (int y = 0; y < companies.get(x).size(); y++)
                for (Job prev_job : new_hire.work_experience)
                    if (prev_job.company_name.equals(companies.get(x).get(y)))
                    {
                        tier_multiplier /= (double) x;
                        tier_multiplier *= (double) prev_job.years;
                        pay_multiplier += tier_multiplier;
                        pay_differential += prev_job.pay * 0.05;
                    }
        }
           
        return ((pay_differential * pay_multiplier) + new_job.pay);
    }
    
    public static class Hire
    {
        String first_name;
        String last_name;
        String education_level;
        ArrayList<Job> work_experience = new ArrayList<>();
    }
    
    public static class Job
    {
        String job_title;
        String company_name;
        double pay;
        int years;              // Years of experience, only applicable to work_experience in Hire class
        
        public Job(String j_t, String c_n, String p, String y)
        {
            job_title = j_t;
            company_name = c_n;
            pay = Double.parseDouble(p);
            years = Integer.parseInt(y);
        }
    }
}