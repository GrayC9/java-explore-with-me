// src/pages/Categories.jsx
import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Snackbar,
  Alert,
  Box,
  Paper
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import { categoryService } from '../api/services';

const Categories = () => {
  const [categories, setCategories] = useState([]);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [categoryName, setCategoryName] = useState('');
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      const response = await categoryService.getCategories();
      setCategories(response.data);
    } catch (error) {
      showSnackbar('Error loading categories', 'error');
    }
  };

  const handleCreateCategory = async () => {
    try {
      await categoryService.createCategory({ name: categoryName });
      setDialogOpen(false);
      setCategoryName('');
      loadCategories();
      showSnackbar('Category created successfully', 'success');
    } catch (error) {
      showSnackbar('Error creating category', 'error');
    }
  };

  const handleUpdateCategory = async () => {
    try {
      await categoryService.updateCategory(editingCategory.id, { name: categoryName });
      setDialogOpen(false);
      setEditingCategory(null);
      setCategoryName('');
      loadCategories();
      showSnackbar('Category updated successfully', 'success');
    } catch (error) {
      showSnackbar('Error updating category', 'error');
    }
  };

  const handleDeleteCategory = async (categoryId) => {
    try {
      await categoryService.deleteCategory(categoryId);
      loadCategories();
      showSnackbar('Category deleted successfully', 'success');
    } catch (error) {
      showSnackbar('Error deleting category', 'error');
    }
  };

  const showSnackbar = (message, severity) => {
    setSnackbar({ open: true, message, severity });
  };

  const openCreateDialog = () => {
    setEditingCategory(null);
    setCategoryName('');
    setDialogOpen(true);
  };

  const openEditDialog = (category) => {
    setEditingCategory(category);
    setCategoryName(category.name);
    setDialogOpen(true);
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4 }}>
        <Typography variant="h4">Categories</Typography>
        <Button variant="contained" onClick={openCreateDialog}>
          Create Category
        </Button>
      </Box>

      <Paper elevation={2}>
        <List>
          {categories.map((category) => (
            <ListItem 
              key={category.id}
              divider
              sx={{
                '&:hover': {
                  backgroundColor: 'rgba(0, 0, 0, 0.04)',
                }
              }}
            >
              <ListItemText 
                primary={category.name} 
                secondary={`ID: ${category.id}`}
              />
              <ListItemSecondaryAction>
                <IconButton 
                  edge="end" 
                  aria-label="edit"
                  onClick={() => openEditDialog(category)}
                  sx={{ mr: 1 }}
                >
                  <EditIcon />
                </IconButton>
                <IconButton 
                  edge="end" 
                  aria-label="delete"
                  onClick={() => handleDeleteCategory(category.id)}
                >
                  <DeleteIcon />
                </IconButton>
              </ListItemSecondaryAction>
            </ListItem>
          ))}
          {categories.length === 0 && (
            <ListItem>
              <ListItemText 
                primary="No categories found" 
                secondary="Create a new category to get started"
              />
            </ListItem>
          )}
        </List>
      </Paper>

      {/* Create/Edit Category Dialog */}
      <Dialog 
        open={dialogOpen} 
        onClose={() => setDialogOpen(false)}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>
          {editingCategory ? 'Edit Category' : 'Create New Category'}
        </DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Category Name"
            fullWidth
            value={categoryName}
            onChange={(e) => setCategoryName(e.target.value)}
            variant="outlined"
            error={categoryName.length > 0 && categoryName.length < 2}
            helperText={
              categoryName.length > 0 && categoryName.length < 2 
                ? 'Category name must be at least 2 characters'
                : ''
            }
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
          <Button 
            onClick={editingCategory ? handleUpdateCategory : handleCreateCategory}
            variant="contained"
            disabled={categoryName.length < 2}
          >
            {editingCategory ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar for notifications */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert 
          onClose={() => setSnackbar({ ...snackbar, open: false })}
          severity={snackbar.severity}
          variant="filled"
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default Categories;
