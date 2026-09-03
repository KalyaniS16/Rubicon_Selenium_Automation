@dashboard-cards @scheduled-projects
Feature: Scheduled Projects card

  Background:
    Given the user is logged in on the dashboard

  Scenario: Scheduled Projects card count matches detail page and page actions work
    Then the Scheduled Projects card is visible
    When the user reads the Scheduled Projects count from the dashboard
    And the user opens the Scheduled Projects card
    Then the Scheduled Projects detail page is displayed
    And the Scheduled Projects detail page count is valid
    And the dashboard and detail page Scheduled Projects counts match
    When the user searches for a Scheduled Project
    And the user refreshes the Scheduled Projects detail page
    And the user exports Scheduled Projects
    Then the Scheduled Projects detail page is displayed
