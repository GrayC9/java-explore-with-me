import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
  Snackbar,
  Box,
  Alert
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import { userService } from '../api/services';

const Users = () => {
  const [users, setUsers] = useState([]);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [newUser, setNewUser] = useState({ name: '', email: '' });
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      const response = await userService.getUsers();
      setUsers(response.data);
    } catch (error) {
      showSnackbar('Error loading users', 'error');
    }
  };

  const handleCreateUser = async () => {
    try {
      await userService.createUser(newUser);
      setCreateDialogOpen(false);
      loadUsers();
      showSnackbar('User created successfully', 'success');
    } catch (error) {
      showSnackbar('Error creating user', 'error');
    }
  };

  const handleDeleteUser = async (userId) => {
    try {
      await userService.deleteUser(userId);
      loadUsers();
      showSnackbar('User deleted successfully', 'success');
    } catch (error) {
      showSnackbar('Error deleting user', 'error');
    }
  };

  const showSnackbar = (message, severity) => {
    setSnackbar({ open: true, message, severity });
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4 }}>
        <Typography variant="h4">Users</Typography>
        <Button variant="contained" onClick={() => setCreateDialogOpen(true)}>
          Create User
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((user) => (
              <TableRow key={user.id}>
                <TableCell>{user.id}</TableCell>
                <TableCell>{user.name}</TableCell>
                <TableCell>{user.email}</TableCell>
                <TableCell>
                  <IconButton onClick={() => handleDeleteUser(user.id)}>
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Create User Dialog */}
      <Dialog open={createDialogOpen} onClose={() => setCreateDialogOpen(false)}>
        <DialogTitle>Create New User</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Name"
            value={newUser.name}
            onChange={(e) => setNewUser({ ...newUser, name: e.target.value })}
            margin="normal"
          />
          <TextField
            fullWidth
            label="Email"
            value={newUser.email}
            onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCreateDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleCreateUser} variant="contained">
            Create
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert severity={snackbar.severity} onClose={() => setSnackbar({ ...snackbar, open: false })}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default Users;
