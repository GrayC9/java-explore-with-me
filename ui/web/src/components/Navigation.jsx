import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

const Navigation = () => {
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          Event Manager
        </Typography>
        <Box>
          <Button color="inherit" component={RouterLink} to="/">
            Events
          </Button>
          <Button color="inherit" component={RouterLink} to="/users">
            Users
          </Button>
          <Button color="inherit" component={RouterLink} to="/categories">
            Categories
          </Button>
          <Button color="inherit" component={RouterLink} to="/compilations">
            Compilations
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navigation;
