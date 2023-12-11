import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CreateMessage from '../src/components/CreateMessage';
import Home from '../src/pages/Home';
import { test, expect, describe, it } from 'vitest'

// Testing UI
test("Have a buton call Add Idea", () => {
    render(<CreateMessage />);
    // Expect add idea button to be present
    const button = screen.getByText("Add Idea");
    expect(button).toBeDefined();
});


// Testing Logic
test("should reveal the input form when 'Add Idea' button is clicked", async () => {
    render(<CreateMessage />);
    const addButton = screen.getByText('Add Idea');

    // Simulate clicking on the add button
    userEvent.click(addButton);

    // Wait for the form to appear
    await waitFor(() => {
        // Validate that inputs for title and ideas appear
        const titleInput = screen.getByLabelText('Title');
        const ideaInput = screen.getByLabelText('Idea')
        expect(titleInput).toBeDefined();
        expect(ideaInput).toBeDefined();
    });
});

test("Navbar button should work correctly", async () => {
    render(<Home />);
    // Find the button by its text content
    const navbarButton = screen.getByText('User');
    // Simulate clicking on the Navbar button
    userEvent.click(navbarButton);

});

