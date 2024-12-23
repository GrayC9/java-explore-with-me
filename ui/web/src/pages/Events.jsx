// src/pages/Events.jsx
import React, { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardActions from '@mui/material/CardActions';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';
import Switch from '@mui/material/Switch';
import FormControlLabel from '@mui/material/FormControlLabel';
import { eventService, categoryService } from '../api/services';

const Events = () => {
  const [events, setEvents] = useState([]);
  const [categories, setCategories] = useState([]);
  const [filters, setFilters] = useState({
    title: '',
    paid: '',
    categories: [],
  });
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [newEvent, setNewEvent] = useState({
    title: '',
    annotation: '',
    description: '',
    category: '',
    eventDate: '',
    eventTime: '',
    paid: false,
    participantLimit: 0,
    location: { lat: 0, lon: 0 }
  });
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

  useEffect(() => {
    loadEvents();
    loadCategories();
  }, []);

  const loadEvents = async () => {
    try {
      const response = await eventService.getPublicEvents(filters);
      setEvents(response.data);
    } catch (error) {
      showSnackbar('Error loading events', 'error');
    }
  };

  const loadCategories = async () => {
    try {
      const response = await categoryService.getCategories();
      setCategories(response.data);
    } catch (error) {
      showSnackbar('Error loading categories', 'error');
    }
  };

  const handleCreateEvent = async () => {
    try {
      // Validate date is in the future
      const selectedDate = new Date(`${newEvent.eventDate}T${newEvent.eventTime}`);
      const now = new Date();

      if (selectedDate <= now) {
        showSnackbar('Event date must be in the future', 'error');
        return;
      }

      // Format date to match backend's expected format (yyyy-MM-dd HH:mm:ss)
      const formattedDateTime = `${newEvent.eventDate} ${newEvent.eventTime}:00`;

      const eventData = {
        ...newEvent,
        eventDate: formattedDateTime,
      };

      await eventService.createEvent(1, eventData); // Using userId 1 for demo
      setCreateDialogOpen(false);
      loadEvents();
      showSnackbar('Event created successfully', 'success');
    } catch (error) {
      showSnackbar('Error creating event', 'error');
    }
  };

  const handleFilterChange = (field) => (event) => {
    setFilters({ ...filters, [field]: event.target.value });
  };

  const showSnackbar = (message, severity) => {
    setSnackbar({ open: true, message, severity });
  };

  const formatDateTime = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4 }}>
        <Typography variant="h4">Events</Typography>
        <Button variant="contained" onClick={() => setCreateDialogOpen(true)}>
          Create Event
        </Button>
      </Box>

      {/* Filters */}
      <Box sx={{ mb: 4 }}>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={4}>
            <TextField
              fullWidth
              label="Search"
              value={filters.title}
              onChange={handleFilterChange('title')}
            />
          </Grid>
          <Grid item xs={12} sm={4}>
            <FormControl fullWidth>
              <InputLabel>Paid/Free</InputLabel>
              <Select
                value={filters.paid}
                onChange={handleFilterChange('paid')}
                label="Paid/Free"
              >
                <MenuItem value="">All</MenuItem>
                <MenuItem value="true">Paid</MenuItem>
                <MenuItem value="false">Free</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Button fullWidth variant="outlined" onClick={loadEvents}>
              Apply Filters
            </Button>
          </Grid>
        </Grid>
      </Box>

      {/* Events Grid */}
      <Grid container spacing={3}>
        {events.map((event) => (
          <Grid item xs={12} sm={6} md={4} key={event.id}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {event.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {event.annotation}
                </Typography>
                <Typography variant="body2">
                  Category: {event.category?.name}
                </Typography>
                <Typography variant="body2">
                  Date & Time: {formatDateTime(event.eventDate)}
                </Typography>
                <Typography variant="body2">
                  {event.paid ? 'Paid Event' : 'Free Event'}
                </Typography>
              </CardContent>
              <CardActions>
                <Button size="small">View Details</Button>
                <Button size="small">Register</Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Create Event Dialog */}
      <Dialog
        open={createDialogOpen}
        onClose={() => setCreateDialogOpen(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>Create New Event</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Title"
                value={newEvent.title}
                onChange={(e) => setNewEvent({ ...newEvent, title: e.target.value })}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Annotation"
                multiline
                rows={2}
                value={newEvent.annotation}
                onChange={(e) => setNewEvent({ ...newEvent, annotation: e.target.value })}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Description"
                multiline
                rows={4}
                value={newEvent.description}
                onChange={(e) => setNewEvent({ ...newEvent, description: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Category</InputLabel>
                <Select
                  value={newEvent.category}
                  onChange={(e) => setNewEvent({ ...newEvent, category: e.target.value })}
                  label="Category"
                >
                  {categories.map((category) => (
                    <MenuItem key={category.id} value={category.id}>
                      {category.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Participant Limit"
                type="number"
                value={newEvent.participantLimit}
                onChange={(e) => setNewEvent({ ...newEvent, participantLimit: parseInt(e.target.value) })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Date"
                type="date"
                value={newEvent.eventDate}
                onChange={(e) => setNewEvent({ ...newEvent, eventDate: e.target.value })}
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Time"
                type="time"
                value={newEvent.eventTime}
                onChange={(e) => setNewEvent({ ...newEvent, eventTime: e.target.value })}
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControlLabel
                control={
                  <Switch
                    checked={newEvent.paid}
                    onChange={(e) => setNewEvent({ ...newEvent, paid: e.target.checked })}
                  />
                }
                label="Paid Event"
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCreateDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleCreateEvent} variant="contained">
            Create
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert
          severity={snackbar.severity}
          onClose={() => setSnackbar({ ...snackbar, open: false })}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default Events;
