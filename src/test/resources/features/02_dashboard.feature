@dashboard @smoke
Feature: Dashboard

  Background:
    Given the user is logged in on the dashboard

  Scenario: Dashboard is loaded after login
    Then the dashboard page is displayed
