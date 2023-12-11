import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import MessageList from '../src/components/MessageList';
import {test, expect} from 'vitest';

// Testing UI
test("Have a h3 called All Messages", () => {
    render(<MessageList />);
    const heading = screen.getByText("All Messages");
    expect(heading).toBeDefined();
});


// Cannot test edit, like, and delete buttons as they require a message to be rendered
// Since the tests cannot view our database, it will not be able to see any messages that aren't hardcoded in