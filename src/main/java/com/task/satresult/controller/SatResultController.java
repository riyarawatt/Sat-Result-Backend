package com.task.satresult.controller;

import com.task.satresult.model.SatResult;
import com.task.satresult.repository.SatResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/satresults")
@CrossOrigin(origins = "*")
public class SatResultController {
    private final SatResultRepository satResultRepository;

    @Autowired
    public SatResultController(SatResultRepository satResultRepository) {
        this.satResultRepository = satResultRepository;
    }

    // Create (Insert) a new SAT result
    @PostMapping("/create")
    public SatResult createSatResult(@RequestBody SatResult satResult) {
        int satScore = satResult.getSat_score_percentage();
        boolean passed = satScore > 30;
        satResult.setPassed(passed);

        // Save the SAT result to the database
        return satResultRepository.save(satResult);
    }

    // View all SAT results
    @GetMapping("/view-all")
    public List<SatResult> getAllSatResults() {
        return satResultRepository.findAll();
    }

    @GetMapping("/get-rank/{name}")
    public String getSatResultRank(@PathVariable String name) {
        // Retrieve the SAT result by name from your database or repository
        SatResult satResult = satResultRepository.findByName(name);

        if (satResult != null) {
            // Fetch all SAT results with scores greater than or equal to the current SAT result
            List<SatResult> higherScoreResults = satResultRepository.findBySat_score_percentageGreaterThanEqual(
                    satResult.getSat_score_percentage()
            );

            // Calculate the rank based on the count of higher-scored SAT results
            int rank = higherScoreResults.size();
            String rankSuffix = getRankSuffix(rank);
            return "Rank: " + rank + rankSuffix;
        } else {
            return "SAT result not found";
        }
    }

    // Helper method to get the suffix for the rank (e.g., 1st, 2nd, 3rd)
    private String getRankSuffix(int rank) {
        if (rank >= 11 && rank <= 13) {
            return "th";
        }
        switch (rank % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }


    // Update SAT score for a specific SAT result by name
    @PutMapping("/update-score/{name}")
    public ResponseEntity<?> updateSatResultScore(
            @PathVariable String name,
            @RequestParam int newSatScore) {
        SatResult existingSatResult = satResultRepository.findByName(name);
        if (existingSatResult != null) {
            existingSatResult.setSat_score_percentage(newSatScore);
            existingSatResult.setPassed(newSatScore > 30);
            satResultRepository.save(existingSatResult);
            return ResponseEntity.ok(existingSatResult);
        } else {
            return ResponseEntity.notFound().build(); // Return a 404 Not Found response
        }
    }

    // Delete a specific SAT result by name
    @DeleteMapping("/delete/{name}")
    public ResponseEntity<String> deleteSatResultByName(@PathVariable String name) {
        SatResult existingSatResult = satResultRepository.findByName(name);
        if (existingSatResult != null) {
            satResultRepository.delete(existingSatResult);
            return ResponseEntity.ok("SAT result deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
